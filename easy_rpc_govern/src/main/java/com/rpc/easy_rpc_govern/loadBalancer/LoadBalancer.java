package com.rpc.easy_rpc_govern.loadBalancer;

import com.rpc.easy_rpc_govern.config.RpcConfigProperties;
import com.rpc.domain.bean.ConsumeRequest;
import com.rpc.domain.bean.ServiceMeta;
import com.rpc.easy_rpc_govern.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class LoadBalancer implements LoadBalancerHandler{
    @Resource
    RpcConfigProperties.RPCConsumer consumer;

    public ServiceMeta selectService(List<ServiceMeta> serviceMetas){
        String loadBalance = consumer.getLoadBalance();
        LoadBalanceStrategy loadBalanceStrategy =SpringContextUtil.getBean(loadBalance,LoadBalanceStrategy.class);
        return loadBalanceStrategy.selectServiceMate(serviceMetas);
    }


}
