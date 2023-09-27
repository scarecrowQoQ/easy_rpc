package com.rpc.easy_rpc_provider.service;

import com.rpc.domain.bean.ConsumeRequest;
import com.rpc.domain.bean.ProviderResponse;
import com.rpc.domain.bean.RpcRequestHolder;
import com.rpc.domain.bean.ServiceMeta;

public interface ProviderService {
    /**
     * 注册服务
     * @param
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
    ProviderResponse responseConsume(ConsumeRequest consumeRequest);
}
