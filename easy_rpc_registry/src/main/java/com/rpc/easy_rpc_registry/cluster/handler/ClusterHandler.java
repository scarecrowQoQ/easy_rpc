package com.rpc.easy_rpc_registry.cluster.handler;

import com.rpc.domain.bean.RequestHeader;
import com.rpc.domain.bean.RpcRequestHolder;
import com.rpc.domain.enumeration.RequestType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Service;

/**
 * 作为服务端接受消息后响应
 * 主要包含：
 * 1.对拉票请求进行响应
 * 2.对确认Leader请求进行响应
 * 3.
 */
@Service
@ChannelHandler.Sharable
public class ClusterHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequestHolder rpcRequestHolder = (RpcRequestHolder) msg;
        RequestType type = rpcRequestHolder.getRequestHeader().getType();


        super.channelRead(ctx, msg);
    }
}
