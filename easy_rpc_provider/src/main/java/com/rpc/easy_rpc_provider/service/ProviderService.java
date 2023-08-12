package com.rpc.easy_rpc_provider.service;

import com.rpc.domain.rpc.ConsumeRequest;
import com.rpc.domain.rpc.RpcRequestHolder;
import com.rpc.domain.rpc.ServiceMeta;

import java.util.List;

public interface ProviderService {
    /**
     * 注册服务
     * @param serviceMeta
     * @return
     */
    Boolean registerService(ServiceMeta serviceMeta);

    /**
     * heartBeat 心跳机制
     */
    Boolean sendHeart();
    /**
     * 返回消费请求
     */
    RpcRequestHolder   responseConsume(ConsumeRequest consumeRequest);
}
