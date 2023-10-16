package com.rpc.easy_rpc_registry.cluster.listener;

import com.rpc.easy_rpc_govern.config.RpcConfigProperties;
import com.rpc.easy_rpc_registry.cluster.bean.NodeContent;
import com.rpc.easy_rpc_registry.cluster.enumeration.NodeStatus;
import com.rpc.easy_rpc_registry.cluster.service.ClusterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 节点状态监听器
 * 监听状态变为Candidate，则发起拉票请求
 * 监听状态变成Leader，则发送心跳请求
 */
@Service
@ConditionalOnProperty(
        value="rpc.registry.cluster",
        havingValue = "true",
        matchIfMissing = false)
@Slf4j
public class NodeStatusListener {

    @Resource
    @Lazy
    ClusterService clusterService;

    @Resource
    @Lazy
    NodeContent nodeContent;

    @Resource
    RpcConfigProperties.RpcRegistry registry;

    private static Long maxSleepTime = 2000L;

//  检测到成为Leader，首先需要向其他节点发送Leader身份确认，然后进行日志记录同步，同时开启心跳发送
    public void onBecomingLeader(){
        clusterService.confirmLeaderShip();
        clusterService.updateDataToFollower();
    }

//  检测到成为Follower，开启监听心跳
    public void onBecomingFollower(){
    }

//   检测到状态为初始化 ，直接进入选举流程
    public void onBecomingInception() {
//      清空上一次选举的状态
        nodeContent.setBallot(new AtomicInteger(0));
        nodeContent.setVoted(false);
//      只要节点状态不是leader或者不是follower 则不断进行选举
        while (!nodeContent.getNodeStatus().equals(NodeStatus.FOLLOWER)||!nodeContent.getNodeStatus().equals(NodeStatus.LEADER)){
            log.info("开始本轮选举，当前选期id="+nodeContent.getTermId());
            if(nodeContent.getNodeStatus().equals(NodeStatus.LEADER)||nodeContent.getNodeStatus().equals(NodeStatus.FOLLOWER)){
                log.info("选举结束");
                return;
            }
            Random random = new Random();
//          随机睡眠时间，不超过最大规定时间
            long randomSleepTime = Math.abs(random.nextLong()) % maxSleepTime;
            log.info("随机睡眠"+randomSleepTime+"ms");
            try {
                Thread.sleep(randomSleepTime);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
//          如果已经给其他节点投过票了，则不参与选举
            if(nodeContent.getVoted()){
                log.info("已经给其他节点投过票了,退出本轮选举");
                return;
            }
//          如果睡眠结束后仍然没有进行投票则转为候选者并发起投票
            nodeContent.setNodeStatus(NodeStatus.CANDIDATE);
        }
        log.info("选举结束，当前集群节点的Leader是："+nodeContent.getLeaderAddress());
    }

    /**
     * 成为候选者后先给自己投票，然后向其他节点发起投票请求
     */
    public void onBecomingCandidate() {
        nodeContent.setTermId(nodeContent.getTermId()+1);
        nodeContent.setVoted(true);
        nodeContent.getBallot().incrementAndGet();
        log.info("我要去选举");
        clusterService.askVote();
    }

    public void incrBallot(String voteId){
        clusterService.incrResponseCount(voteId);
    }
}
