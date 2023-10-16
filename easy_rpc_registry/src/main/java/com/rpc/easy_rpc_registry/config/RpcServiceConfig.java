package com.rpc.easy_rpc_registry.config;

import com.rpc.domain.bean.RpcServiceList;
import com.rpc.easy_rpc_protocol.cach.ConnectCache;
import com.rpc.easy_rpc_protocol.coder.NettyDecoder;
import com.rpc.easy_rpc_protocol.coder.NettyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcServiceConfig   {
    @Bean
    public RpcServiceList rpcServiceList(){
        return new RpcServiceList();
    }


}
