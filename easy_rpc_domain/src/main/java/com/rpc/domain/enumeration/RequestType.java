package com.rpc.domain.enumeration;

public enum RequestType {
//    注册服务
    SEND_SERVICE,
//    拉取服务
    GET_SERVICE,
//    消费服务
     CONSUME_SERVICE,
//    响应服务
    RESPONSE_SERVICE,
//    发送心跳
    SEND_HEARTBEAT,

//    以下为集群相关

//  leader心跳
    LEADER_HEARTBEAT,

//  响应ack
    RESPONSE_ACK,

//  请求投票
    ASK_VOTE,

//  投票
    VOTE,

//  向节点确认自己leader身份
    CONFIRM_LEADER,

//   更新节点服务数据
    UPDATE_DATA

}
