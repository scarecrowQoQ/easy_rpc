package com.rpc.easy_rpc_consumer.consumer;

import com.rpc.easy_rpc_consumer.service.ConsumerService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;


@ComponentScan({"com.rpc.easy_rpc_consumer","com.rpc.easy_rpc_govern", "com.rpc.easy_rpc_protocol"})
public class ConsumerProcessor implements InitializingBean{

    @Resource
    ConsumerService consumerService;

    @Override
    public void afterPropertiesSet() {
//        服务列表拉取
        consumerService.getServiceList();
    }


}
