package com.rpc.easy_rpc_consumer.consumer;

import com.rpc.easy_rpc_consumer.annotation.RpcConsumer;
import com.rpc.easy_rpc_consumer.service.ConsumerService;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;


@ComponentScan({"com.rpc.easy_rpc_consumer", "com.rpc.domain"})
public class ConsumerProcessor implements InitializingBean{

    @Resource
    ConsumerService consumerService;

    @Override
    public void afterPropertiesSet() throws Exception {
//        服务列表拉取
        consumerService.getServiceList();
    }


}
