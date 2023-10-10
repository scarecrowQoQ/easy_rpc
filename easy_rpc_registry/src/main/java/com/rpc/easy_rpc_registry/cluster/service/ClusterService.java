package com.rpc.easy_rpc_registry.cluster.service;

public interface ClusterService {
    /**
     * 心跳监测，用于leader向follower发送心跳来监测leader可用
     */
    public void sendHeartBeat();

    /**
     * 向其他节点请求拉票
     */
    public void askVote();

    /**
     * 向其他其他节点投票
     */
    public void vote();

    /**
     * 向其他节点确认自己leader身份
     */
    public void confirmLeaderShip();

    /**
     * 向从节点进行数据同步
     */
    public void synchronousDataToFollower();

    /**
     * 更新从leader发布的数据
     */
    public void synchronousDataFromLeader();

    /**
     * 返回一个确认消息
     */
    public void sendACK();
}
