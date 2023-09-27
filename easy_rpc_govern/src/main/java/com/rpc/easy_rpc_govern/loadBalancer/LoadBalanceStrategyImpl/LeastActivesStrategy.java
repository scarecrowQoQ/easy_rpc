package com.rpc.easy_rpc_govern.loadBalancer.LoadBalanceStrategyImpl;

import com.rpc.domain.bean.ServiceMeta;
import com.rpc.easy_rpc_govern.loadBalancer.LoadBalanceStrategy;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Component("leastActives")
public class LeastActivesStrategy implements LoadBalanceStrategy {

    @Override
    public ServiceMeta selectServiceMate(List<ServiceMeta> ServiceMetas) {
        List<ServiceMeta> collect = ServiceMetas.stream().sorted(Comparator.comparing(ServiceMeta::getActives)).collect(Collectors.toList());
        ServiceMeta serviceMeta = collect.get(0);
        serviceMeta.setActives(serviceMeta.getActives()+1);
        int i = ServiceMetas.indexOf(serviceMeta);
        ServiceMetas.set(i,serviceMeta);
        return collect.get(0);
    }
}
