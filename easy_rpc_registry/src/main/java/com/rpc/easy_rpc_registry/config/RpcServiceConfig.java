package com.rpc.easy_rpc_registry.config;

import com.rpc.domain.bean.RpcServiceList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcServiceConfig   {
    @Bean
    public RpcServiceList rpcServiceList(){
        RpcServiceList rpcServiceList = new RpcServiceList();
        return rpcServiceList;
    }
}
