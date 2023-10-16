package com.rpc.easy_rpc_govern.context;

import com.rpc.domain.annotation.RpcConsumer;
import com.rpc.domain.bean.*;
import com.rpc.easy_rpc_govern.utils.SpringContextUtil;
import com.rpc.easy_rpc_govern.Interceptor.RequestInterceptHandler;
import com.rpc.easy_rpc_govern.filter.RequestFilterHandler;
import com.rpc.easy_rpc_govern.filter.ResponseFilterHandler;
import com.rpc.easy_rpc_govern.fuse.FuseHandler;
import com.rpc.easy_rpc_govern.limit.LimitHandler;
import com.rpc.easy_rpc_govern.limit.entity.LimitingRule;
import com.rpc.easy_rpc_govern.loadBalancer.LoadBalancerHandler;
import com.rpc.easy_rpc_govern.config.RpcConfigProperties;
import com.rpc.easy_rpc_govern.route.RouteHandler;
import com.rpc.domain.enumeration.RequestType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

@Service
@Scope(value="prototype")
@Slf4j
@Data
public class RpcPipeline {

    private FuseHandler fuseHandler;

    private LimitHandler limitHandler;

    private RouteHandler routeHandler;

    private LoadBalancerHandler loadBalancerHandler;

    private RequestFilterHandler requestFilterHandler;

    private RequestInterceptHandler requestInterceptHandler;

    private ResponseFilterHandler responseFilterHandler;

    private RpcConfigProperties.RPCConsumer rpcConsumer;




    @Autowired
    public RpcPipeline(FuseHandler fuseHandler, LimitHandler limitHandler,
                       LoadBalancerHandler loadBalancerHandler, RequestFilterHandler requestFilterHandler,
                       ResponseFilterHandler responseFilterHandler, RpcConfigProperties.RPCConsumer rpcConsumer,
                       RouteHandler routeHandler,
                       RequestInterceptHandler requestInterceptHandler) {
        this.fuseHandler = fuseHandler;
        this.limitHandler = limitHandler;
        this.loadBalancerHandler = loadBalancerHandler;
        this.requestFilterHandler = requestFilterHandler;
        this.responseFilterHandler = responseFilterHandler;
        this.rpcConsumer = rpcConsumer;
        this.routeHandler = routeHandler;
        this.requestInterceptHandler = requestInterceptHandler;
    }



    /**
     * 首先经过限流器判断
     * 然后式熔断器判断
     * 然后经过路由器进行服务注入
     * @return
     */
    public void preSend(RpcConsumer consumerAnnotation,LimitingRule rule) {
        RpcContext rpcContext = RpcContext.getContext();
        Boolean pass = null;
//        限流层处理
        if(rule!=null){
            pass = this.limitHandler.limitHandle(rule);
            if(!pass){
//              降级处理
                rpcContext.setSupplier(()-> fallBackHandler(rule.getFallBack(), rpcContext.getConsumeRequest()));
                rpcContext.setSend(false);
                return;
            }
        }
//      熔断层处理
        if(rpcConsumer.fuseEnable){
            pass = fuseHandler.fuseHandle(consumerAnnotation.serviceName());
            if (!pass) {
                rpcContext.setSupplier(()-> fallBackHandler(consumerAnnotation.fallback(),rpcContext.getConsumeRequest()));
                rpcContext.setSend(false);
                return;
            }
        }

        ServiceMeta targetService = null;

//      路由层处理
        List<ServiceMeta> serviceMetas = routeHandler.routeHandle(rpcContext.getCandidateServiceList());

//      负载均衡处理
        if(rpcConsumer.loadBalanceEnable){
            targetService = loadBalancerHandler.selectService(serviceMetas);
        }else {
            targetService = serviceMetas.get(0);
        }
//      目标服务注入
        rpcContext.setRemoteService(targetService);
//      请求过滤器处理
        requestFilterHandler.filterHandler();
//      请求拦截层处理
        pass = requestInterceptHandler.interceptorHandle(rpcContext);
        if (!pass){
            rpcContext.setSend(false);
            return;
        }
//        可以发送
        rpcContext.setSend(true);
    }



    private Object fallBackHandler(Class<?> fallBack, ConsumeRequest consumeRequest){
        if(fallBack != void.class){
            try {
                Object bean = SpringContextUtil.getBean(fallBack);
                Method method1 = fallBack.getMethod(consumeRequest.getMethodName(),consumeRequest.getParameterTypes());
                Object res = method1.invoke(bean, consumeRequest.getArgs());
                 return res;
            }catch (Exception e){
                log.error("限流降级执行错误!");
                e.printStackTrace();
            }
        }
        return null;
    }


}
