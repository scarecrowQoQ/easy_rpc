package com.rpc.easy_rpc_govern.filter;

import com.rpc.easy_rpc_govern.context.RpcContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
public class ResponseFilterDefault implements ResponseFilterHandler{
    @Override
    public RpcContext filterHandler(RpcContext rpcContext) {
        return rpcContext;
    }
}
