package com.rpc.easy_rpc_consumer.service;
import com.rpc.domain.rpc.CommonHeader;
import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.protocol.enum2.RequestType;
import com.rpc.domain.rpc.*;
import com.rpc.easy_rpc_consumer.cach.ResponseCache;
import com.rpc.domain.rpc.ServiceListHolder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
        ServiceMeta service = serviceListHolder.getService(consumeRequest.getServiceName());
        String providerHost = service.getServiceHost();
        int providerPort = service.getServicePort();
        String beanName = service.getBeanName();
        consumeRequest.setBeanName(beanName);
        CommonHeader header = new CommonHeader(RequestType.CONSUME_SERVICE);
        ChannelFuture channelFuture = connectTargetService(providerHost, providerPort);
        if(channelFuture != null){
            Channel channel = channelFuture.channel();
            RpcRequestHolder requestHolder = new RpcRequestHolder(header,consumeRequest);
            try {
                channel.writeAndFlush(requestHolder);
                if (channelFuture.isDone()) {
//                    这里无法拿到响应结果，需要从handler代码中将结果放入缓存中，再根据id来拿去结果
                    ProviderResponse result = responseCache.getResult(consumeRequest.getRequestId());
                    log.info(result.toString());
                    return result.getResult();
                }else {
                    log.error("获取响应数据失败");
                }
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
            System.out.println("连接失败，请检查服务端是否开启");
            e.printStackTrace();
        }
        if (channelFuture!=null && channelFuture.isSuccess()) {
            return channelFuture;
        }else {
            return null;
        }
    }
}
