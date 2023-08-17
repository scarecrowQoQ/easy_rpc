package com.rpc.server_demo;

import com.rpc.easy_rpc_core.annotation.EnableEasyRPC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEasyRPC
public class ServerDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerDemoApplication.class, args);
    }
}
