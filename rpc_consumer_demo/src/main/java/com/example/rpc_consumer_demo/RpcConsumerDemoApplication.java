package com.example.rpc_consumer_demo;

import com.rpc.easy_rpc_consumer.annotation.EnableRpcConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpcConsumer
public class RpcConsumerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcConsumerDemoApplication.class, args);
    }
}
