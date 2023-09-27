package com.rpc.consumer1.service;

import com.rpc.projectapi.orderApi.OrderService;
import com.rpc.domain.annotation.RpcConsumer;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @RpcConsumer(serviceName = "orderService")
    OrderService orderService;


    public String getMyOrder(){
       return orderService.getOrder();
    }

    public String getMyOrderByAsync(){
        System.out.println(orderService.getOrderAsync());

//        RpcContext context = RpcContext.getContext();
        orderService.getOrder();
//        CompletableFuture<String> callback = AsyncContext.getCallback(() -> orderService.getOrderAsync());
//        callback.whenComplete((result,error)->{
//            System.out.println(result);
//        });
//        System.out.println("111");
        return "";
    }
}
