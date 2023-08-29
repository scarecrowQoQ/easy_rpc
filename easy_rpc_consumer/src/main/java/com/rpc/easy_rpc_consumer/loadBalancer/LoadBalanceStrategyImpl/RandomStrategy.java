package com.rpc.easy_rpc_consumer.loadBalancer.LoadBalanceStrategyImpl;

import com.rpc.domain.protocol.bean.ServiceMeta;
import com.rpc.easy_rpc_consumer.loadBalancer.LoadBalanceStrategy;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Random;
@Component("random")
public class RandomStrategy implements LoadBalanceStrategy {
    @Override
    public ServiceMeta selectServiceMate(List<ServiceMeta> ServiceMetas) {
        Random random = new Random();
        int i = random.nextInt(ServiceMetas.size());
        return ServiceMetas.get(i);
    }
}
