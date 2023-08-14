package com.rpc.easy_rpc_consumer.netServer;

import com.rpc.domain.config.RpcProperties;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
@Component
@RequiredArgsConstructor
public class NettyServerStarter  {
    @Resource
    RpcProperties.RPCServer server;
    @Resource
    Bootstrap bootstrap;


}
