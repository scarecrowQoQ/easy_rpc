package com.rpc.easy_rpc_govern.route;

import com.rpc.domain.bean.ConsumeRequest;
import com.rpc.domain.bean.RpcServiceList;
import com.rpc.domain.bean.ServiceMeta;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class RouteServer implements RouteHandler {
    @Override
    public List<ServiceMeta> routeHandle(List<ServiceMeta> serviceMetas) {
        return serviceMetas;
    }
}
