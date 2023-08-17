package com.example.provider_demo2.api;

import com.rpc.easy_rpc_provider.annotation.EasyRpcProvider;
import org.springframework.stereotype.Service;

//@EasyRpcProvider(serviceName = "order")
@Service
public class TestApiImpl2 implements TestApi{
    private int flag = 0;
    @Override
    public String getOrder() throws InterruptedException {
        System.out.println("getOrder方法执行");
        if(flag == 1){
            Thread.sleep(4000);
        }

        return "调用成功1";
    }

    @Override
    public int startSleep() {
        this.flag = 1;
        return 0;
    }
}
