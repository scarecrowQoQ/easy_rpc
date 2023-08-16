package com.example.rpc_consumer_demo.service;

import com.example.rpc_consumer_demo.fuse.TestFuse;
import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.utils.SpringContextUtil;
import com.rpc.easy_rpc_consumer.annotation.RpcConsumer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestServiceImpl{

    @RpcConsumer(serviceName = "test", fallback = TestFuse.class)
    private TestService testService;


    @RequestMapping("/test")
    public void test()  {
       String test = testService.test();
       System.out.println("test="+test);
   }

    @RequestMapping("/sleep")
    public void sleep()  {
       testService.startSleep();
        System.out.println("开启睡眠");
    }
}
