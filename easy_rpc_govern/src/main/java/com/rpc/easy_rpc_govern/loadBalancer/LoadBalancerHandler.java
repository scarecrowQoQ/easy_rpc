package com.rpc.easy_rpc_govern.loadBalancer;

import com.rpc.domain.bean.ConsumeRequest;
import com.rpc.domain.bean.ServiceMeta;

import java.util.List;

public interface LoadBalancerHandler {

    public ServiceMeta selectService(List<ServiceMeta> serviceMetas);
}
