package com.example.rpc_consumer_demo.service;

import com.example.rpc_consumer_demo.entity.Person;
import com.example.rpc_consumer_demo.fuse.TestFuse;
import com.rpc.domain.limit.limitAnnotation.LimitingStrategy;
import com.rpc.easy_rpc_consumer.annotation.RpcConsumer;
import org.springframework.web.bind.annotation.RequestBody;
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
    @LimitingStrategy(strategyName = "test",limitKey = "id",QPS = 3)
    public void getOrder( Person p)  {
        System.out.println("执行放行");
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
