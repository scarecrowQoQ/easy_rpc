package com.rpc.consumer1.controller;

import com.rpc.consumer1.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/myOrder")
    public String getMyOrder(){
        return userService.getMyOrder();
    }

    @GetMapping("/myOrderAsync")
    public String getMyOrderByAsync(){
        return userService.getMyOrderByAsync();
    }
}
