package com.rpc.easy_rpc_consumer.service;
import com.rpc.domain.rpc.CommonHeader;
import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.protocol.enum2.RequestType;
import com.rpc.domain.rpc.*;
import com.rpc.easy_rpc_consumer.cach.ResponseCache;
import com.rpc.domain.rpc.ServiceListHolder;
import com.rpc.easy_rpc_consumer.loadBalancer.LoadBalancer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class ConsumerServiceImpl implements ConsumerService{

    @Resource
    Bootstrap bootstrap;

    @Resource
    RpcProperties.RPCServer server;

    @Resource
    ServiceListHolder serviceListHolder;

    @Resource
    ResponseCache responseCache;

    @Resource
    LoadBalancer loadBalancer;


    /**
     * 执行逻辑
     * 1: 根据本地服务缓存列表查询远程服务地址（负载均衡策略）
     * 2：补全consumeRequest属性 beanName
     * 3: 建立连接，封装RpcRequestHolder，发送请求
     * @param consumeRequest
     * @return
     */
    @Override
    public Object sendRequest(ConsumeRequest consumeRequest) {
        String requestId = consumeRequest.getRequestId();
        List<ServiceMeta> services = serviceListHolder.getService(consumeRequest.getServiceName());
        log.info("当前可选服务"+services);
        ServiceMeta service = loadBalancer.selectService(services);
        log.info("当前选择服务"+service.toString());
        String providerHost = service.getServiceHost();
        int providerPort = service.getServicePort();
        String beanName = service.getBeanName();
        consumeRequest.setBeanName(beanName);
        CommonHeader header = new CommonHeader(RequestType.CONSUME_SERVICE);
        ChannelFuture channelFuture = connectTargetService(providerHost, providerPort);
        if(channelFuture != null){
            Channel channel = channelFuture.channel();
            Promise<ProviderResponse> promise = new DefaultPromise<>(new DefaultEventLoop());
            responseCache.putPromise(requestId,promise);
            RpcRequestHolder requestHolder = new RpcRequestHolder(header,consumeRequest);
            try {
                channel.writeAndFlush(requestHolder);
//              这里无法拿到响应结果，需要从handler代码中将结果放入缓存中，再根据id来拿去结果
                ProviderResponse result = responseCache.getResult(promise);
                responseCache.removeResponse(requestId);
                log.info("执行结果响应："+result.toString());
                return result.getResult();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     *  进行服务列表获取
     * @return
     */
    @Override
    public ServiceListHolder getServiceList() {
        CommonHeader header = new CommonHeader(RequestType.GET_SERVICE);
        RpcRequestHolder requestHolder = new RpcRequestHolder(header,null);
        ChannelFuture channelFuture = connectTargetService(server.getHost(), server.getPort());
        if(channelFuture != null){
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(requestHolder);
        }
        return null;
    }

    /**
     * 指定端口进行连接
     * @param host
     * @param port
     * @return
     */
    private ChannelFuture connectTargetService(String host,int port){
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.connect(host, port).sync();

        }catch (Exception e){
            log.error("连接失败，请检查服务端是否开启目标Host:"+host+"port:"+port);
            e.printStackTrace();
        }
        if (channelFuture!=null && channelFuture.isSuccess()) {
            log.info("连接成功,目标Host:"+host+"port:"+port);
            return channelFuture;
        }else {
            return null;
        }
    }
}
