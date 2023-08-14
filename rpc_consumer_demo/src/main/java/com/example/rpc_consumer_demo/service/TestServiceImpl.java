package com.example.rpc_consumer_demo.service;

import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.utils.SpringContextUtil;
import com.rpc.easy_rpc_consumer.annotation.RpcConsumer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestServiceImpl implements InitializingBean{

    @RpcConsumer(serviceName = "test")
    private TestService testService;


   @RequestMapping("/test")
    public void afterPropertiesSet2() throws Exception {
       RpcProperties.RPCServer server = SpringContextUtil.getBean(RpcProperties.RPCServer.class);
       System.out.println(server.getHost());
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
