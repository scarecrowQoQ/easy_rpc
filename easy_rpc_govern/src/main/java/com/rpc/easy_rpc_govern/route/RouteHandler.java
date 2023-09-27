package com.rpc.easy_rpc_govern.route;

import com.rpc.domain.bean.ConsumeRequest;
import com.rpc.domain.bean.RpcServiceList;
import com.rpc.domain.bean.ServiceMeta;

import java.util.List;

public interface RouteHandler {
    public List<ServiceMeta> routeHandle(List<ServiceMeta> serviceMetas);
}
