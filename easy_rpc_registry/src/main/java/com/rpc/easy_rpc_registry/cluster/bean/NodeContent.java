package com.rpc.easy_rpc_registry.cluster.bean;

import com.rpc.domain.bean.HeartBeat;
import com.rpc.easy_rpc_registry.cluster.enumeration.NodeStatus;
import com.rpc.easy_rpc_registry.cluster.listener.NodeStatusListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@Slf4j
public class NodeContent {
//  本节点地址
    private String address;

//  节点状态
    private volatile NodeStatus nodeStatus;

//  选期id
    private Integer termId;

//  当前Leader
    private String leaderAddress;

//  集群节点数量
    private Integer clusterNodeAmount;

//  当前选期的选票数
    private AtomicInteger ballot;

//  状态监听器
    private NodeStatusListener nodeStatusListener;

//  是否已经给其他节点进行了投票
    private volatile Boolean voted;

//  Leader心跳，用于检测Leader是否存活
    private HeartBeat leaderHeartBeat;

    public void setNodeStatus(NodeStatus nodeStatus) {
        log.info("我现在的身份是"+nodeStatus);
        this.nodeStatus = nodeStatus;
        if(nodeStatus.equals(NodeStatus.INITIAL)){
            nodeStatusListener.onBecomingInception();
        }else if(nodeStatus.equals(NodeStatus.LEADER)){
            nodeStatusListener.onBecomingLeader();
        }else if(nodeStatus.equals(NodeStatus.FOLLOWER)){
            nodeStatusListener.onBecomingFollower();
        }else if(nodeStatus.equals(NodeStatus.CANDIDATE)){
            nodeStatusListener.onBecomingCandidate();
        }
    }
}
