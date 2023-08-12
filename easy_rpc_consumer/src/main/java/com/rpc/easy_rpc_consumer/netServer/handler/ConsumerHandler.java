package com.rpc.easy_rpc_consumer.netServer.handler;

import com.rpc.easy_rpc_consumer.service.ConsumerService;
import com.rpc.domain.rpc.RpcRequestHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
@ChannelHandler.Sharable
public class ConsumerHandler extends ChannelInboundHandlerAdapter {

    private ConsumerService consumerService;

    /**
     * 这里进行服务提供者返回数据接收
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequestHolder requestHolder = (RpcRequestHolder) msg;
    }

    /**
     * 这里进行服务列表获取
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {


        super.channelActive(ctx);
    }
}
