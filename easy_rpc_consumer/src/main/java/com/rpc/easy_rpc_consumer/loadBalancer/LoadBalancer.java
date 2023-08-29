package com.rpc.easy_rpc_consumer.loadBalancer;

import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.protocol.bean.ServiceMeta;
import com.rpc.domain.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class LoadBalancer {
    @Resource
    RpcProperties.RPCConsumer consumer;

    public ServiceMeta selectService(List<ServiceMeta> serviceMetas){
        String loadBalance = consumer.getLoadBalance();
        LoadBalanceStrategy loadBalanceStrategy =SpringContextUtil.getBean(loadBalance,LoadBalanceStrategy.class);
        return loadBalanceStrategy.selectServiceMate(serviceMetas);
    }


}
