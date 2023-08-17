package com.rpc.provider_demo;

import com.rpc.domain.config.RpcProperties;
import com.rpc.easy_rpc_provider.annotation.EnableRpcProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;

@SpringBootApplication
@EnableRpcProvider
public class ProviderDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderDemoApplication.class, args);
    }
}
