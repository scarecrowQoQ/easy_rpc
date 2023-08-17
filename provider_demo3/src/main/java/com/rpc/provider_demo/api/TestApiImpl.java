package com.rpc.provider_demo.api;

import com.rpc.easy_rpc_provider.annotation.EasyRpcProvider;
import org.springframework.stereotype.Service;

@EasyRpcProvider(serviceName = "test")
@Service
public class TestApiImpl implements TestApi{
    private int flag = 0;
    @Override
    public String test() throws InterruptedException {
        System.out.println("服务2");
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
