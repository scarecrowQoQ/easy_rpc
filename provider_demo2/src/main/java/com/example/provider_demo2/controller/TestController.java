package com.example.provider_demo2.controller;

import com.rpc.domain.config.RpcProperties;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class TestController {
    @Resource
    RpcProperties rpcProperties;

}
