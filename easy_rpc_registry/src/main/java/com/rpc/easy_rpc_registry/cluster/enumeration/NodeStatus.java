package com.rpc.easy_rpc_registry.cluster.enumeration;

public enum NodeStatus {
//  领导
    LEADER,
//  跟随者
    FOLLOWER,
//  候选者
    CANDIDATE,
//  初始(系统初始化或者失去Leader时的状态)
    INITIAL;
}
