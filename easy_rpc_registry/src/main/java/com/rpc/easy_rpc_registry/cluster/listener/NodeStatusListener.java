package com.rpc.easy_rpc_registry.cluster.listener;

import com.rpc.easy_rpc_registry.cluster.bean.NodeContent;
import com.rpc.easy_rpc_registry.cluster.enumeration.NodeStatus;
import com.rpc.easy_rpc_registry.cluster.service.ClusterService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Random;

/**
 * 节点状态监听器
 * 监听状态变为Candidate，则发起拉票请求
 * 监听状态变成Leader，则发送心跳请求
 */
@Service
public class NodeStatusListener {

    @Resource
    ClusterService clusterService;

    @Resource
    NodeContent nodeContent;

    private static Long maxSleepTime = 200L;


    public void sendLeaderHeartBeat(){

    }

    public void checkLeaderHeartBeat(){

    }
    public void electLeader() {
//      只要节点状态不是leader或者不是follower 则不断进行选举
        while (!nodeContent.getNodeStatus().equals(NodeStatus.FOLLOWER)||!nodeContent.getNodeStatus().equals(NodeStatus.LEADER)){
            Random random = new Random();
//          随机睡眠时间，不超过最大规定时间
            long randomSleepTime = Math.abs(random.nextLong()) % maxSleepTime;
            try {
                Thread.sleep(randomSleepTime);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
//          如果已经给其他节点投过票了，则不参与选举
            if(nodeContent.getVoted()){
                return;
            }
//          如果睡眠结束后仍然没有进行投票则转为候选者并发起投票
            nodeContent.setNodeStatus(NodeStatus.CANDIDATE);
//          发起拉票请求
            clusterService.askVote();

        }
    }

}
