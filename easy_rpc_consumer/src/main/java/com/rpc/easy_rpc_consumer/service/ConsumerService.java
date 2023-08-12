package com.rpc.easy_rpc_consumer.service;

import com.rpc.domain.rpc.ConsumeRequest;
import com.rpc.domain.rpc.ServiceListHolder;

public interface ConsumerService {
    /**
     * 消费者发送消费请求给服务提供者
     * @return
     */
    public void sendRequest(ConsumeRequest consumeRequest);

    /**
     * 消费者获取服务列表
     * @return
     */
    public ServiceListHolder getServiceList();
}
