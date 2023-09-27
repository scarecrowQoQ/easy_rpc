package com.rpc.provider1.service;

import com.rpc.projectapi.orderApi.OrderService;
import com.rpc.domain.annotation.EasyRpcProvider;
import org.springframework.stereotype.Service;

@Service
@EasyRpcProvider(serviceName = "orderService")
public class OrderServiceImpl implements OrderService {
    @Override
    public String getOrder(){
        return "这是你的订单，来自provider-1";
    }

    @Override
    public String getOrderAsync() {
        return "异步获取订单成功";
    }
}
