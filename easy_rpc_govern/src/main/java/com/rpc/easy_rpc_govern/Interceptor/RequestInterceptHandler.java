package com.rpc.easy_rpc_govern.Interceptor;

import com.rpc.easy_rpc_govern.context.RpcContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;


public interface RequestInterceptHandler {
    public Boolean interceptorHandle(RpcContext context);
}
