package com.rpc.easy_rpc_provider.nettyServer.handler;

import com.rpc.easy_rpc_provider.service.ProviderService;
import com.rpc.domain.protocol.enum2.RequestType;
import com.rpc.domain.protocol.bean.CommonHeader;
import com.rpc.domain.protocol.bean.ConsumeRequest;
import com.rpc.domain.protocol.bean.RpcRequestHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
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
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequestHolder requestHolder = (RpcRequestHolder) msg;
        CommonHeader commonHeader = requestHolder.getCommonHeader();
        RequestType type = commonHeader.getType();
        if (type.equals(RequestType.CONSUME_SERVICE)) {
            ConsumeRequest consumeRequest = (ConsumeRequest) requestHolder.getData();
            RpcRequestHolder response = registerService.responseConsume(consumeRequest);
            ctx.writeAndFlush(response);
        }else if (type.equals(RequestType.GET_SERVICE)){
            registerService.registerService();
        }
    }
}
