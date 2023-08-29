package com.example.provider1.service;

import com.example.api.orderApi.OrderService;
import com.rpc.easy_rpc_provider.annotation.EasyRpcProvider;
import org.springframework.stereotype.Service;

@Service
@EasyRpcProvider(serviceName = "orderService")
public class OrderServiceImpl implements OrderService {
    @Override
    public String getOrder(){
        return "这是你的订单，来自provider-1";
    }
}
