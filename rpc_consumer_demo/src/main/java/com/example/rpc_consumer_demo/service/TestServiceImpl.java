package com.example.rpc_consumer_demo.service;

import com.example.rpc_consumer_demo.fuse.TestFuse;
import com.rpc.easy_rpc_consumer.annotation.RpcConsumer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestServiceImpl{

    @RpcConsumer(serviceName = "test", fallback = TestFuse.class)
    private TestService testService;

    @RpcConsumer(serviceName = "order")
    private order order;

    @RequestMapping("/test")
    public void test()  {
       String test = testService.test();
       System.out.println("test="+test);
   }

    @RequestMapping("/order")
    public void getOrder()  {
        String test = order.getOrder();
        System.out.println("test="+test);
    }

    @RequestMapping("/sleep")
    public void sleep()  {
       testService.startSleep();
        System.out.println("开启睡眠");
    }

    @RequestMapping("/ordersleep")
    public void ordersleep()  {
        order.startSleep();
        System.out.println("开启睡眠");
    }
}
