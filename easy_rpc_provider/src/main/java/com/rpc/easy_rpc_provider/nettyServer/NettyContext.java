package com.rpc.easy_rpc_provider.nettyServer;

import io.netty.channel.ChannelFuture;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
/**
 * Netty上下文对象，完成
 */
public class NettyContext {

    private ChannelFuture channelFuture;

}
