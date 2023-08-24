package com.example.provider_demo2;

import com.rpc.easy_rpc_provider.annotation.EnableRpcProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpcProvider
public class ProviderDemo2Application {

    public static void main(String[] args) {
        SpringApplication.run(ProviderDemo2Application.class, args);
    }

}
