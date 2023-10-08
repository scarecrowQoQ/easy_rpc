package com.rpc.provider2.service;

import com.rpc.projectapi.orderApi.OrderService;
import com.rpc.domain.annotation.EasyRpcProvider;
import org.springframework.stereotype.Service;
@Service
@EasyRpcProvider(serviceName = "orderService")
public class OrderServiceImpl implements OrderService {
    @Override
    public String getOrder(){
        return "这是你的订单，来自provider-2";
    }

    @Override
    public String getOrderAsync() {
        return null;
    }
}
