package com.rpc.easy_rpc_registry.cluster.service;

import com.rpc.domain.bean.HeartBeat;
import com.rpc.domain.bean.RequestHeader;
import com.rpc.domain.bean.RpcRequestHolder;
import com.rpc.domain.bean.RpcServiceList;
import com.rpc.domain.enumeration.RequestType;
import com.rpc.easy_rpc_govern.config.RpcConfigProperties;
import com.rpc.easy_rpc_protocol.cach.ConnectCache;
import com.rpc.easy_rpc_registry.cluster.bean.NodeContent;
import com.rpc.easy_rpc_registry.cluster.bean.VoteRequest;
import com.rpc.easy_rpc_registry.cluster.bean.VoteResponse;
import com.rpc.easy_rpc_registry.cluster.enumeration.NodeStatus;
import com.rpc.easy_rpc_registry.utils.ConnectUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@ConditionalOnProperty(
        value="rpc.registry.cluster",
        havingValue = "true",
        matchIfMissing = false)
@Slf4j
public class ClusterServiceImpl implements ClusterService {

    @Resource
    RpcConfigProperties.RpcRegistry rpcRegistry;

    @Autowired
    @Qualifier("clusterBootStrap")
    Bootstrap bootstrap;

    @Resource
    @Lazy
    NodeContent nodeContent;

    @Resource
    @Qualifier("clusterConnectCache")
    ConnectCache clusterConnectCache;

    @Resource
    RpcServiceList rpcServiceList;

    @Resource
    @Qualifier("taskExecutor")
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private final ConcurrentHashMap<String, AtomicInteger> responseCount = new ConcurrentHashMap<>();

    private final Lock lock = new ReentrantLock();

    private final Condition condition = lock.newCondition();

    private static final Semaphore vote = new Semaphore(1);

    @Override
    public void askVote() {
        VoteRequest voteRequest = new VoteRequest(rpcRegistry.host+":"+rpcRegistry.port,nodeContent.getTermId());
        RequestHeader requestHeader = new RequestHeader(RequestType.ASK_VOTE,true);
        RpcRequestHolder rpcRequestHolder = new RpcRequestHolder(requestHeader,voteRequest);
        sendDataToOtherNode(rpcRequestHolder);
        log.info("开始同步阻塞等待本轮投票结束");
        syncWaitResponse(voteRequest.getVoteId(),1000l,nodeContent.getClusterNodeAmount()-1);
        log.info("本轮投票结束，当前票数"+nodeContent.getBallot());
        //       当前的选票数量大于集群节点数量/2，则身份变为Leader
        if(nodeContent.getBallot().get()>=nodeContent.getClusterNodeAmount()/2+1){
            nodeContent.setNodeStatus(NodeStatus.LEADER);
        }
    }

    /**
     * 对于一个投票需要进行安全检查后才认为有效，以下认定拉票为无效情况
     * 1：当前节点已经是候选者的情况下投票无效
     * 2：拉票请求的选期id不大于当前节点的选期id，则认为无效
     * 3：已经给其他一个节点进行了投票，则认为无效
     */
    @Override
    public RpcRequestHolder vote(VoteRequest voteRequest) {
        int termId = voteRequest.getTermId();
        RequestHeader header = new RequestHeader(RequestType.VOTE);
//       对于选期不大于当前选期的投票请求拒绝,对于当前身份已经是Leader拒绝，对于已为其他节点投票进行拒绝
        if(nodeContent.getTermId()>=termId||nodeContent.getNodeStatus().equals(NodeStatus.LEADER)||nodeContent.getVoted()){
            VoteResponse voteResponse = new VoteResponse(nodeContent.getAddress(),termId,voteRequest.getVoteId(), Boolean.FALSE);
            return new RpcRequestHolder(header,voteResponse);
        }else {
            //      进行并发控制。
            try {
                vote.acquire();
                log.info("我收到了来自"+voteRequest.getInitiator()+"的拉票,我为他投票");
                VoteResponse voteResponse = new VoteResponse(nodeContent.getAddress(),termId,voteRequest.getVoteId(),Boolean.TRUE);
                nodeContent.setVoted(true);
                return new RpcRequestHolder(header,voteResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }finally {
                vote.release();
            }
        }
    }

    @Override
    public void confirmLeaderShip() {
        RequestHeader requestHeader = new RequestHeader(RequestType.CONFIRM_LEADER,true);
        RpcRequestHolder rpcRequestHolder = new RpcRequestHolder(requestHeader,new HeartBeat(nodeContent.getAddress()));
        sendDataToOtherNode(rpcRequestHolder);
    }

    @Override
    public void updateDataToFollower() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("termId",nodeContent.getTermId());
        RequestHeader requestHeader = new RequestHeader(RequestType.UPDATE_DATA,attrs);
        RpcRequestHolder rpcRequestHolder = new RpcRequestHolder(requestHeader,rpcServiceList);
        sendDataToOtherNode(rpcRequestHolder);

    }

    @Override
    public void updateDataFromLeader(RpcServiceList serviceList) {
        rpcServiceList.setServiceList(serviceList.getServiceList());

    }

    @Override
    public void sendACK() {

    }

    /**
     * 向其他集群节点发送数据
     */
    private void sendDataToOtherNode(RpcRequestHolder rpcRequestHolder){
//
        List<String> cluster = rpcRegistry.getClusterAddress();
        for (String nodeAddress : cluster) {
            String host = nodeAddress.split(":")[0];
            int port = Integer.parseInt(nodeAddress.split(":")[1]);
            if (rpcRegistry.getHost().equals(host)&& rpcRegistry.getPort().equals(port)) {
                continue;
            }
            ChannelFuture connect = ConnectUtil.connect(host, port,bootstrap,clusterConnectCache);
            if(connect == null){
                continue;
            }
            connect.channel().writeAndFlush(rpcRequestHolder);

        }
    }

    private void syncWaitResponse(String id, long timeout, int expectNum){
        long start = System.currentTimeMillis();
        while (true) {
            AtomicInteger count = responseCount.get(id);
            if (count != null && count.get() >= expectNum) {
                log.info("同步结束，耗时:"+(System.currentTimeMillis() - start));
                return;
            }
            lock.lock();
            try {
                if (!condition.await(timeout, TimeUnit.MILLISECONDS)) {
                    log.info("同步结束，耗时超时:"+(System.currentTimeMillis() - start));
                    return;
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            finally {
                lock.unlock();
            }
            if (System.currentTimeMillis() - start >= timeout) {
                return;
            }
        }
    }
    @Override
    public void incrResponseCount(String voteId){
        responseCount.compute(voteId, (key, value) -> {
            if (value == null) {
                return new AtomicInteger(1);
            } else {
                value.incrementAndGet();
                return value;
            }
        });
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }



    /**
     * 用于确认返回的ACK数量是否大于集群节点一般
     */
    private void confirmACKCounts(){

    }


    public void initCluster(){
        threadPoolTaskExecutor.execute(()->{
            List<String> cluster = rpcRegistry.getClusterAddress();
            for (String nodeAddress : cluster) {
                String host = nodeAddress.split(":")[0];
                int port = Integer.parseInt(nodeAddress.split(":")[1]);
                if (rpcRegistry.getHost().equals(host)&& rpcRegistry.getPort().equals(port)) {
                    continue;
                }
                ChannelFuture connect = null;
                while(connect == null){
                    try {
                        connect = ConnectUtil.connect(host, port,bootstrap,clusterConnectCache);
                        Thread.sleep(200L);
                    } catch (InterruptedException e) {
                        log.error("集群节点："+host+":"+port+"重连中");
                    }
                }
                log.info("节点："+host+"："+port +"连接成功！");
            }
            log.info("节点全部连接成功!开始初始化集群");
            nodeContent.setNodeStatus(NodeStatus.INITIAL);
        });
    }

    @Override
    @Scheduled(fixedDelayString  = "#{RpcRegistry.sendLeaderHeartBeatTime}")
    public void EnableLeaderHeartBeat() {
        if(nodeContent.getNodeStatus()==null||nodeContent.getNodeStatus()!=NodeStatus.LEADER){
            return;
        }
        RequestHeader requestHeader = new RequestHeader(RequestType.LEADER_HEARTBEAT);
        HeartBeat leaderHeartBeat = new HeartBeat();
        RpcRequestHolder rpcRequestHolder = new RpcRequestHolder(requestHeader,leaderHeartBeat);
        sendDataToOtherNode(rpcRequestHolder);
    }

    @Override
    @Scheduled(fixedDelayString  = "#{RpcRegistry.checkLeaderHeartBeatTime}")
    public void EnableListenLeaderHeartBeat() {
        if(nodeContent.getNodeStatus()==null||nodeContent.getNodeStatus()!=NodeStatus.FOLLOWER){
            return;
        }

        HeartBeat leaderHeartBeat = nodeContent.getLeaderHeartBeat();
        Long lastUpdateTime = leaderHeartBeat.getUpdateTime();
        log.info("检查Leader心跳"+leaderHeartBeat);
//      上一次的心跳时间大于检测时间，则认为Leader宕机，节点状态转为初始化
        if(System.currentTimeMillis() - lastUpdateTime > rpcRegistry.checkLeaderHeartBeatTime){
            nodeContent.setNodeStatus(NodeStatus.INITIAL);
        }
    }
}
