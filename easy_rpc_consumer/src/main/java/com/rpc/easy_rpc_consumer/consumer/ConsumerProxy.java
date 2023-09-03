package com.rpc.easy_rpc_consumer.consumer;

import com.rpc.easy_rpc_consumer.annotation.RpcConsumer;
import com.rpc.easy_rpc_consumer.service.ConsumerService;
import com.rpc.domain.protocol.bean.ConsumeRequest;
import com.rpc.domain.utils.SpringContextUtil;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ConsumerProxy implements InvocationHandler{

    private final String serviceName;

    private Class<?> fallBack;

    public ConsumerProxy(RpcConsumer rpcConsumer){
        this.serviceName = rpcConsumer.serviceName();
        this.fallBack = rpcConsumer.fallback();
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
        Class<?> returnType = method.getReturnType();

        consumeRequest.setParameters(args);
        consumeRequest.setParameterTypes(method.getParameterTypes());
        ConsumerService service = SpringContextUtil.getBean(ConsumerService.class);
//        log.info("执行消费请求"+consumeRequest.toString());
            try {
                CompletableFuture completableFuture = (CompletableFuture) service.sendRequest(consumeRequest,true);
                return completableFuture;
            }catch (Exception e){
                e.printStackTrace();
            }

//        }else {
//            Object result = service.sendRequest(consumeRequest,false);
////        如果成功响应（非熔断/拦截状态）
//            if(result != null){
//                return result;
//            }
//        }


//      非正常响应，检查是否有降级类，执行降级方法
        if(fallBack != void.class){
            try {
                Object bean = SpringContextUtil.getBean(fallBack);
                Method method1 = fallBack.getMethod(method.getName(), method.getParameterTypes());
                Object res = method1.invoke(bean, args);
                return res;
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            log.error("连接超时！！没有熔断类!");

        }
        return null;
    }
}
