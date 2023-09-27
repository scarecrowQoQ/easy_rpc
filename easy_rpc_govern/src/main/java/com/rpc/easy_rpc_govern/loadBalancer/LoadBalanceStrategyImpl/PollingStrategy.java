package com.rpc.easy_rpc_govern.loadBalancer.LoadBalanceStrategyImpl;

import com.rpc.domain.bean.ServiceMeta;
import com.rpc.easy_rpc_govern.loadBalancer.LoadBalanceStrategy;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
@Component("polling")
public class PollingStrategy implements LoadBalanceStrategy {

    private AtomicInteger index = new AtomicInteger(0);

    @Override
    public ServiceMeta selectServiceMate(List<ServiceMeta> ServiceMetas) {
        int curIndex = index.get()%ServiceMetas.size();
        ServiceMeta serviceMeta = ServiceMetas.get(curIndex);
        index.incrementAndGet();
        return serviceMeta;
    }
}
