package com.rpc.easy_rpc_govern.filter;

import com.rpc.domain.bean.ConsumeRequest;
import com.rpc.domain.bean.RequestHeader;

/**
 * 调用拦截器
 */
public interface RequestFilterHandler {
    public void filterHandler();
}
