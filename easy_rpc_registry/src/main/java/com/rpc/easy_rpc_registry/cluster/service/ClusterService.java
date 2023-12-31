package com.rpc.easy_rpc_registry.cluster.service;

import com.rpc.domain.bean.RpcRequestHolder;
import com.rpc.domain.bean.RpcServiceList;
import com.rpc.easy_rpc_registry.cluster.bean.VoteRequest;
import com.rpc.easy_rpc_registry.cluster.bean.VoteResponse;

public interface ClusterService {
    /**
     * 向其他节点请求拉票
     */
    public void askVote();

    /**
     * 向其他其他节点投票
     */
    public RpcRequestHolder vote(VoteRequest voteRequest);

    /**
     * 向其他节点确认自己leader身份
     */
    public void confirmLeaderShip();

    /**
     * 向从节点进行数据同步
     */
    public void updateDataToFollower();

    /**
     * 更新从leader发布的数据
     */
    public void updateDataFromLeader(RpcServiceList serviceList);

    /**
     * 返回一个确认消息
     */
    public void sendACK();

    /**
     * 同步等待一个请求
     */
    public void incrResponseCount(String requestId);

    /**
     * 初始集群
     */
    public void initCluster();

    /**
     * 心跳监测，用于leader向follower发送心跳来监测leader可用
     */
    public void EnableLeaderHeartBeat();

    /**
     * 开启Leader心跳监测
     */
    public void EnableListenLeaderHeartBeat();
}
