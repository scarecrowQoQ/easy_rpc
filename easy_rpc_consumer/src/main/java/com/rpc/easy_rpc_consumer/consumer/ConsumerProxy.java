package com.rpc.easy_rpc_consumer.consumer;

import com.rpc.easy_rpc_consumer.annotation.RpcConsumer;
import com.rpc.easy_rpc_consumer.service.ConsumerService;
import com.rpc.domain.rpc.ConsumeRequest;
import com.rpc.domain.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
@Slf4j
public class ConsumerProxy implements InvocationHandler{

    private final String serviceName;

    private Class<?> fallBack;

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
        log.info("执行消费请求"+consumeRequest.toString());

        Object result = service.sendRequest(consumeRequest);
//        如果成功响应（非熔断/拦截状态）
        if(result != null){
            return result;
        }
//        检查是否有降级类，执行降级方法
        if(fallBack != void.class){
            FastClass fastClass = FastClass.create(fallBack);
            int methodIndex = fastClass.getIndex(method.getName(), method.getParameterTypes());
            Object invoke = fastClass.invoke(methodIndex, proxy, args);
            return invoke;
        }else{
            log.error("连接超时！！没有熔断类!");
            return null;
        }
    }
}
