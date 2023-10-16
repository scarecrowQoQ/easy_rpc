package com.rpc.easy_rpc_registry.nettyServer.handler;

import com.rpc.domain.bean.RequestHeader;
import com.rpc.domain.bean.RpcRequestHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.springframework.stereotype.Service;

@Service
@ChannelHandler.Sharable

public class RegistryOutBoundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }
}
