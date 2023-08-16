package com.rpc.easy_rpc_consumer.loadBalancer;

import com.rpc.domain.rpc.ServiceMeta;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface LoadBalanceStrategy {
    ServiceMeta selectServiceMate(List<ServiceMeta> ServiceMetas);
}
