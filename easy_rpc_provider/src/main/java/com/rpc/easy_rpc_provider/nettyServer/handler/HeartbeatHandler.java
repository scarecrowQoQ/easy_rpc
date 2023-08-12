package com.rpc.easy_rpc_provider.nettyServer.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("HEARTBEAT", CharsetUtil.UTF_8));

    private static final int HEARTBEAT_INTERVAL = 5;

    private ScheduledFuture<?> heartbeat;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        heartbeat = ctx.executor().scheduleAtFixedRate(new HeartbeatTask(ctx), 0, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (heartbeat != null) {
            heartbeat.cancel(false);
        }
        ctx.fireChannelInactive();
    }

    private static final class HeartbeatTask implements Runnable {
        private final ChannelHandlerContext ctx;

        public HeartbeatTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
    }
}
