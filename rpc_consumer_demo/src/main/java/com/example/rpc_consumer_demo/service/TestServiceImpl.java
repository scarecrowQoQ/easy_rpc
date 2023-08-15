package com.example.rpc_consumer_demo.service;

import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.utils.SpringContextUtil;
import com.rpc.easy_rpc_consumer.annotation.RpcConsumer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestServiceImpl implements InitializingBean{

    @RpcConsumer(serviceName = "test")
    private TestService testService;


   @RequestMapping("/test")
    public void afterPropertiesSet2() throws Exception {
       String test = testService.test();
       System.out.println("test="+test);
   }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
