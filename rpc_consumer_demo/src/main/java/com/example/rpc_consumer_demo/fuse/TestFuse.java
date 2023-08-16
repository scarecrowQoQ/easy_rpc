package com.example.rpc_consumer_demo.fuse;

import org.springframework.stereotype.Component;

@Component
public class TestFuse {
    public String test(){
        System.out.println("服务降级处理");
        return "服务降级处理";
    }
}
