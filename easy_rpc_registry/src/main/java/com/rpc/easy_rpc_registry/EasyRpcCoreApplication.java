package com.rpc.easy_rpc_registry;

import com.rpc.easy_rpc_registry.annotation.EnableEasyRPC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.rpc")
@EnableEasyRPC
public class EasyRpcCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyRpcCoreApplication.class, args);

    }
}
