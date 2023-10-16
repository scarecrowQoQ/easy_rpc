package com.example.registry2;

import com.rpc.easy_rpc_registry.annotation.EnableEasyRPC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEasyRPC
public class Registry2Application {

    public static void main(String[] args) {
        SpringApplication.run(Registry2Application.class, args);
    }

}
