package com.rpc.easy_rpc_consumer.consumer;

import com.rpc.easy_rpc_consumer.netServer.handler.ConsumerHandler;
import com.rpc.easy_rpc_govern.context.RpcContext;
import com.rpc.easy_rpc_govern.context.RpcPipeline;
import com.rpc.easy_rpc_govern.limit.entity.LimitingRule;
import com.rpc.easy_rpc_govern.limit.limitAnnotation.LimitingStrategy;
import com.rpc.domain.annotation.RpcConsumer;
import com.rpc.easy_rpc_govern.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import com.rpc.domain.bean.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class ConsumerProxy implements InvocationHandler{

    RpcConsumer rpcConsumer = null;

    public ConsumerProxy(RpcConsumer rpcConsumer){

        this.rpcConsumer = rpcConsumer;

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
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcPipeline rpcPipeline = SpringContextUtil.getBean(RpcPipeline.class);
        ConsumerHandler consumerHandler = SpringContextUtil.getBean(ConsumerHandler.class);
        RpcServiceList rpcServiceList = SpringContextUtil.getBean(RpcServiceList.class);
//        消费请求初始化
        ConsumeRequest consumeRequest = new ConsumeRequest(rpcConsumer.serviceName(),method.getName(),method.getParameterTypes(),args);
//      限流数据获取
        LimitingStrategy limitingStrategy = method.getAnnotation(LimitingStrategy.class);
        LimitingRule rule = null;
        if(limitingStrategy != null){
             rule = new LimitingRule(limitingStrategy,args);
        }
//        注入属性
        RpcContext context = RpcContext.getContext();
        context.setServiceName(rpcConsumer.serviceName());
        context.setCandidateServiceList(rpcServiceList.getServiceByName(rpcConsumer.serviceName()));
        context.setConsumeRequest(consumeRequest);
        context.setMaxRetryTime(rpcConsumer.maxRetryTime());
        context.setTimeout(rpcConsumer.timeout());
        context.setGroup(rpcConsumer.group());
//      执行rpcPipeline构造RpcContext
        rpcPipeline.preSend(rpcConsumer,rule);
//      执行send方法
        return consumerHandler.sendRequest();

    }
}
