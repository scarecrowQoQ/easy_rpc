package com.rpc.easy_rpc_govern.filter;

import com.rpc.easy_rpc_govern.context.RpcContext;

public interface ResponseFilterHandler {
    public RpcContext filterHandler(RpcContext rpcContext);

}
