package com.example.consumer1.service;

import com.example.projectapi.orderApi.OrderService;
import com.rpc.easy_rpc_consumer.annotation.RpcConsumer;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class UserService {
    @RpcConsumer(serviceName = "orderService")
    OrderService orderService;

    public String getMyOrder(){
       return orderService.getOrder();
    }

    public String getMyOrderByAsync(){
        System.out.println(orderService.getOrderAsync());
        CompletableFuture orderAsync = (CompletableFuture) orderService.getOrderAsync();

        orderAsync.whenComplete((result,error)->{
            System.out.println(result);
        });
        System.out.println("111");
        return "";
    }
}
