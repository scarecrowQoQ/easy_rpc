package com.example.provider1.service;

import com.example.projectapi.orderApi.OrderService;
import com.rpc.easy_rpc_provider.annotation.EasyRpcProvider;
import io.netty.util.concurrent.Promise;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@EasyRpcProvider(serviceName = "orderService")
public class OrderServiceImpl implements OrderService {
    @Override
    public String getOrder(){
        return "这是你的订单，来自provider-1";
    }

    @Override
    public Object getOrderAsync() {

        return "异步获取订单成功";
    }
}
