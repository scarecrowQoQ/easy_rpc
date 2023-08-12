package com.rpc.easy_rpc_provider;

import com.rpc.easy_rpc_provider.annotation.EnableRpcProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpcProvider
public class EasyRpcProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyRpcProviderApplication.class, args);
    }

}
