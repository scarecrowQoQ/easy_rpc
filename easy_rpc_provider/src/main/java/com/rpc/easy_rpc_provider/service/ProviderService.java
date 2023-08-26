package com.rpc.easy_rpc_provider.service;

import com.rpc.domain.rpc.ConsumeRequest;
import com.rpc.domain.rpc.RpcRequestHolder;
import com.rpc.domain.rpc.ServiceMeta;

public interface ProviderService {
    /**
     * 注册服务
     * @param serviceMeta
     * @return
     */
    Boolean registerService() ;

    void addServiceMeta(ServiceMeta serviceMeta);

    /**
     * heartBeat 心跳机制
     */
    void sendHeart();
    /**
     * 返回消费请求
     */
    RpcRequestHolder responseConsume(ConsumeRequest consumeRequest);
}
