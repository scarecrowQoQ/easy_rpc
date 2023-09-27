package com.rpc.easy_rpc_govern.loadBalancer;

import com.rpc.domain.bean.ServiceMeta;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface LoadBalanceStrategy {
    ServiceMeta selectServiceMate(List<ServiceMeta> ServiceMetas);
}
