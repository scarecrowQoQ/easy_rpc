package com.example.provider2.service;

import com.example.projectapi.orderApi.OrderService;
import com.rpc.easy_rpc_provider.annotation.EasyRpcProvider;
import org.springframework.stereotype.Service;
@Service
@EasyRpcProvider(serviceName = "orderService")
public class OrderServiceImpl implements OrderService {
    @Override
    public String getOrder(){
        return "这是你的订单，来自provider-2";
    }

    @Override
    public Object getOrderAsync() {
        return null;
    }
}
