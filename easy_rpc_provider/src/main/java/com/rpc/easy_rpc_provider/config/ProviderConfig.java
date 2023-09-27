package com.rpc.easy_rpc_provider.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class ProviderConfig {
    /**
     * 此配置用于保存服务吗与bean之间的映射关系，方便执行
     * @return
     */
    @Bean(name = "serviceBean")
    public HashMap<String,Object> serviceBean(){
        return new HashMap<>();
    }

}
