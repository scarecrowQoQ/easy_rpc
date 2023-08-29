package com.example.consumer1.service;

import com.example.api.orderApi.OrderService;
import com.rpc.easy_rpc_consumer.annotation.RpcConsumer;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @RpcConsumer(serviceName = "orderService")
    OrderService orderService;

    public String getMyOrder(){
       return orderService.getOrder();
    }
}
