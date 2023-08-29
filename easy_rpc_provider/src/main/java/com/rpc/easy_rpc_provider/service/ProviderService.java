package com.rpc.easy_rpc_provider.service;

import com.rpc.domain.protocol.bean.ConsumeRequest;
import com.rpc.domain.protocol.bean.RpcRequestHolder;
import com.rpc.domain.protocol.bean.ServiceMeta;

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
