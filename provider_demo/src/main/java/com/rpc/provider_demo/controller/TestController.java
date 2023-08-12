package com.rpc.provider_demo.controller;

import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.protocol.serialization.SerializationFactory;
import com.rpc.domain.protocol.serialization.serializationImpl.HessianSerialization;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class TestController {
    @Resource
    RpcProperties rpcProperties;

}
