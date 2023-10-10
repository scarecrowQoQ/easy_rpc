package com.rpc.easy_rpc_registry.cluster.service;

import com.rpc.domain.bean.RequestHeader;
import com.rpc.domain.bean.RpcRequestHolder;
import com.rpc.domain.enumeration.RequestType;
import com.rpc.easy_rpc_govern.config.RpcConfigProperties;
import com.rpc.easy_rpc_registry.cluster.bean.LeaderHeartBeat;
import com.rpc.easy_rpc_registry.cluster.bean.NodeContent;
import com.rpc.easy_rpc_registry.cluster.bean.VoteRequest;
import com.rpc.easy_rpc_registry.utils.ConnectUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class ClusterServiceImpl implements ClusterService {

    @Resource
    RpcConfigProperties.RpcRegistry rpcRegistry;

    @Autowired
    @Qualifier("clusterBootStrap")
    Bootstrap bootstrap;

    @Resource
    NodeContent nodeContent;

    @Override
    public void sendHeartBeat() {
        RequestHeader requestHeader = new RequestHeader(RequestType.LeaderHeartBeat);
        LeaderHeartBeat leaderHeartBeat = new LeaderHeartBeat();
        RpcRequestHolder rpcRequestHolder = new RpcRequestHolder(requestHeader,leaderHeartBeat);
        sendDataToOtherNode(rpcRequestHolder);
    }


    @Override
    public void askVote() {
        VoteRequest voteRequest = new VoteRequest();
        voteRequest.setTermId(nodeContent.getTermId()+1);
        voteRequest.setInitiator(rpcRegistry.host+rpcRegistry.port);
        voteRequest.setCreateTime(new Date());
        RequestHeader requestHeader = new RequestHeader(RequestType.ASK_VOTE);
        RpcRequestHolder rpcRequestHolder = new RpcRequestHolder(requestHeader,voteRequest);
        sendDataToOtherNode(rpcRequestHolder);
    }

    @Override
    public void vote() {

    }

    @Override
    public void confirmLeaderShip() {

    }

    @Override
    public void synchronousDataToFollower() {

    }

    @Override
    public void synchronousDataFromLeader() {

    }

    @Override
    public void sendACK() {

    }

    /**
     * 向其他集群节点发送数据
     * @param data
     */
    private void sendDataToOtherNode(Object data){
        List<String> cluster = rpcRegistry.getCluster();
        for (String nodeAddress : cluster) {
            String host = nodeAddress.split(":")[0];
            int port = Integer.parseInt(nodeAddress.split(":")[1]);
            if (rpcRegistry.getHost().equals(host)&& rpcRegistry.getPort().equals(port)) {
                continue;
            }
            ChannelFuture connect = ConnectUtil.connect(host, port,bootstrap);
            if(connect == null){
                continue;
            }
            connect.channel().writeAndFlush(data);
        }
    }
}
