package com.rpc.easy_rpc_registry.cluster.bean;

import com.rpc.easy_rpc_registry.cluster.enumeration.NodeStatus;
import com.rpc.easy_rpc_registry.cluster.listener.NodeStatusListener;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class NodeContent {

//  节点状态
    private volatile NodeStatus nodeStatus;

//  选期id
    private int termId;

//  当前Leader
    private String leaderId;

    private NodeStatusListener nodeStatusListener;

//  是否已经给其他节点进行了投票
    private volatile Boolean Voted;

    public void setNodeStatus(NodeStatus nodeStatus) {
        this.nodeStatus = nodeStatus;
        if(nodeStatus.equals(NodeStatus.inception)){
            nodeStatusListener.electLeader();
        }else if(nodeStatus.equals(NodeStatus.LEADER)){
            nodeStatusListener.sendLeaderHeartBeat();
        }else if(nodeStatus.equals(NodeStatus.FOLLOWER)){
            nodeStatusListener.checkLeaderHeartBeat();
        }

    }
}
