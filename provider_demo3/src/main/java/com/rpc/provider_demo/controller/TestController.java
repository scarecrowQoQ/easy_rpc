package com.rpc.provider_demo.controller;

import com.rpc.domain.config.RpcProperties;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class TestController {
    @Resource
    RpcProperties rpcProperties;

}
