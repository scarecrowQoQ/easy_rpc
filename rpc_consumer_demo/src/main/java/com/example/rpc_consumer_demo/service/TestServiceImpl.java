package com.example.rpc_consumer_demo.service;

import com.rpc.easy_rpc_consumer.annotation.RpcConsumer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestServiceImpl  {

    @RpcConsumer(serviceName = "test")
    private TestService testService;


   @RequestMapping("/test")
    public void afterPropertiesSet() throws Exception {
        testService.test();
    }
}
