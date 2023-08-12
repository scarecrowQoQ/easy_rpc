package com.rpc.easy_rpc_core.nettyServer.handler;

import com.rpc.domain.protocol.enum2.RequestType;
import com.rpc.domain.rpc.CommonHeader;
import com.rpc.domain.rpc.ServiceListHolder;
import com.rpc.domain.rpc.RpcRequestHolder;
import com.rpc.domain.rpc.ServiceMeta;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ChannelHandler.Sharable
public class NettyHandlerInit extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequestHolder requestHolder = (RpcRequestHolder) msg;
        System.out.println(requestHolder);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接成功");
    }

}
