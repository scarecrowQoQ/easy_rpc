package com.rpc.easy_rpc_govern.context;

import com.rpc.domain.annotation.RpcConsumer;
import com.rpc.domain.bean.*;

import com.rpc.domain.enumeration.RequestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcContext {

    public static ThreadLocal<RpcContext> threadLocal = new ThreadLocal<RpcContext>();

//  上下文参数
    private HashMap<Object,Object> attrs;

//  目标服务名
    private String serviceName;

//  远程选择的服务
    private ServiceMeta remoteService;

//  候选的服务列表
    private List<ServiceMeta> candidateServiceList;

//  传输的数据
    private ConsumeRequest consumeRequest;

//  请求头
    private  RequestHeader requestHeader;

//  失败处理
    Supplier<Object> supplier;

//  重试次数
    private int maxRetryTime;

//  超时时间
    private Long timeout;

//  消费分组
    private String group;

//  是否发送
    private Boolean send;

    public static RpcContext getContext(){
        RpcContext rpcContext = threadLocal.get();
        if(rpcContext == null){
            threadLocal.remove();
            rpcContext  = new RpcContext();
            rpcContext.setAttrs(new HashMap<>());
            rpcContext.setRequestHeader(new RequestHeader(RequestType.CONSUME_SERVICE));
            threadLocal.set(rpcContext);
        }
        return rpcContext;
    }







}
