package com.rpc.easy_rpc_govern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.rpc.easy_rpc_govern","com.rpc.domain"})
public class EasyRpcGovernApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyRpcGovernApplication.class, args);
    }

}
