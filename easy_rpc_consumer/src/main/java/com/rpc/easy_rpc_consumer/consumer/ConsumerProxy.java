package com.rpc.easy_rpc_consumer.consumer;

import com.rpc.easy_rpc_consumer.annotation.RpcConsumer;
import com.rpc.easy_rpc_consumer.service.ConsumerService;
import com.rpc.domain.rpc.ConsumeRequest;
import com.rpc.domain.utils.SpringContextUtil;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ConsumerProxy implements InvocationHandler{

    private  final String serviceName;



    public ConsumerProxy(RpcConsumer rpcConsumer){
        this.serviceName = rpcConsumer.serviceName();
    }

    /**
     * 核心！
     * 此方法用户真正执行服务请求
     * 1: 构建请求头，设置类型为服务消费类型
     * 2：构建请求体，创建一个ConsumeRequest
     * 3: 根据rpcConsumer 获取指定服务名
     * 4：更加服务名查询本地服务列表然后进行构建ConsumeRequest
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ConsumeRequest consumeRequest = new ConsumeRequest();
        consumeRequest.setServiceName(serviceName);
        consumeRequest.setMethodName(method.getName());
        consumeRequest.setParameters(args);
        consumeRequest.setParameterTypes(method.getParameterTypes());
        ConsumerService service = SpringContextUtil.getBean(ConsumerService.class);
        System.out.println("你在执行以下消费请求");
        System.out.println(consumeRequest);
        return service.sendRequest(consumeRequest);
    }
}
