package com.rpc.easy_rpc_consumer.netServer.handler;

import com.rpc.domain.protocol.enum2.RequestType;
import com.rpc.domain.rpc.CommonHeader;
import com.rpc.domain.rpc.ProviderResponse;
import com.rpc.easy_rpc_consumer.cach.ResponseCache;
import com.rpc.domain.rpc.ServiceListHolder;
import com.rpc.easy_rpc_consumer.service.ConsumerService;
import com.rpc.domain.rpc.RpcRequestHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@ChannelHandler.Sharable
@Component
@Slf4j
public class ConsumerHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private ConsumerService consumerService;

    @Resource
    ServiceListHolder serviceListHolder;

    @Resource
    ResponseCache responseCache;


    /**
     * 这里进行服务提供者返回数据接收
     * 将接受到的数据放入响应缓存中，方便在service层中获取响应数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequestHolder requestHolder = (RpcRequestHolder) msg;
        CommonHeader commonHeader = requestHolder.getCommonHeader();
        if(commonHeader.getType() == RequestType.RESPONSE_SERVICE){
            ProviderResponse providerResponse = (ProviderResponse) requestHolder.getData();
            responseCache.addResult(providerResponse.getRequestId(),providerResponse);
        }

    }

    /**
     * 这里进行服务列表获取
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("注册中心连接成功，开始服务列表拉取");
        ServiceListHolder newServiceList = consumerService.getServiceList();
        serviceListHolder.setServiceList(newServiceList.getServiceList());
    }
}
