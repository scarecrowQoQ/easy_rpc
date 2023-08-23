package com.example.rpc_consumer_demo.limitRule;

import com.example.rpc_consumer_demo.entity.Person;
import com.rpc.domain.limit.entity.LimitingRule;
import com.rpc.domain.limit.handler.SelectLimitKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyLimitRule {
    @Bean("test")
    LimitingRule limitingRule(){
        return new LimitingRule("test", 3);
    }
}
