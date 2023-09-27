package com.rpc.easy_rpc_provider.bean;

import com.rpc.domain.bean.RpcServiceList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceListConfig {
    @Bean
    public RpcServiceList rpcServiceList(){
        RpcServiceList rpcServiceList = new RpcServiceList();
        return rpcServiceList;
    }
}
