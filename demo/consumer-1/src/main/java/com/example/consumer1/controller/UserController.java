package com.example.consumer1.controller;

import com.example.consumer1.service.UserService;
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
}
