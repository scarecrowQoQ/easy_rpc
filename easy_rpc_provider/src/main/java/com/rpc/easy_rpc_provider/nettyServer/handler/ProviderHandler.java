package com.rpc.easy_rpc_provider.nettyServer.handler;

import com.rpc.domain.bean.ProviderResponse;
import com.rpc.easy_rpc_provider.service.ProviderService;

import com.rpc.domain.bean.RequestHeader;
import com.rpc.domain.bean.ConsumeRequest;
import com.rpc.domain.bean.RpcRequestHolder;
import com.rpc.domain.enumeration.RequestType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
@Slf4j
@ChannelHandler.Sharable
public class ProviderHandler extends ChannelInboundHandlerAdapter {

    @Resource
    @Lazy
    ProviderService registerService;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        RpcRequestHolder requestHolder = (RpcRequestHolder) msg;
        RequestHeader commonHeader = requestHolder.getRequestHeader();
        RequestType type = commonHeader.getType();
        String requestId = commonHeader.getRequestId();
        if (type.equals(RequestType.CONSUME_SERVICE)) {
            ConsumeRequest consumeRequest = (ConsumeRequest) requestHolder.getData();
            ProviderResponse providerResponse = registerService.responseConsume(consumeRequest);
            RequestHeader header = new RequestHeader(RequestType.RESPONSE_SERVICE,requestId);
            RpcRequestHolder response = new RpcRequestHolder(header,providerResponse);
            ctx.writeAndFlush(response);
        }else if (type.equals(RequestType.GET_SERVICE)){
            registerService.registerService();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接成功");
    }
}
