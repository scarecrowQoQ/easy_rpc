package com.rpc.domain.limit.limitEnum;

public enum BlockStrategy {
//    立即拒绝
    IMMEDIATE_REFUSE,
//    冷启动
    WARM_UP,
//    匀速排队
    UNIFORM_QUEUE
}
