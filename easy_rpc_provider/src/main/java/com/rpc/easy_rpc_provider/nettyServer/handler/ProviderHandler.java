package com.rpc.easy_rpc_provider.nettyServer.handler;

import com.rpc.easy_rpc_provider.service.ProviderService;
import com.rpc.domain.protocol.enum2.RequestType;
import com.rpc.domain.rpc.CommonHeader;
import com.rpc.domain.rpc.ConsumeRequest;
import com.rpc.domain.rpc.RpcRequestHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
@ChannelHandler.Sharable
public class ProviderHandler extends SimpleChannelInboundHandler<RpcRequestHolder> {

    @Resource
    ProviderService registerService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequestHolder requestHolder) throws Exception {
        CommonHeader commonHeader = requestHolder.getCommonHeader();
        if (commonHeader.getType().equals(RequestType.CONSUME_SERVICE)) {
            ConsumeRequest consumeRequest = (ConsumeRequest) requestHolder.getData();
            RpcRequestHolder response = registerService.responseConsume(consumeRequest);
            channelHandlerContext.writeAndFlush(response);
        }
    }


}
