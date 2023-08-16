package com.rpc.provider_demo.api;

import com.rpc.easy_rpc_provider.annotation.EasyRpcProvider;
import org.springframework.stereotype.Service;

@EasyRpcProvider(serviceName = "test")
@Service
public class TestApiImpl2 implements TestApi{
    @Override
    public String test() {
        System.out.println("目标方法执行2");
        return "调用成功2";
    }
}
