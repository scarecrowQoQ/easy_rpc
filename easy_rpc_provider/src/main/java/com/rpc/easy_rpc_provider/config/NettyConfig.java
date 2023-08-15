package com.rpc.easy_rpc_provider.config;

import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.protocol.coder.NettyDecoder;
import com.rpc.domain.protocol.coder.NettyEncoder;
import com.rpc.easy_rpc_provider.nettyServer.handler.ProviderHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

@Configuration
@Data
@Component
public class NettyConfig {


    @Resource
    RpcProperties.provider provider;

    @Resource
    ProviderHandler providerHandler;

    @Bean
    public Bootstrap Bootstrap(){
        Bootstrap bs = new Bootstrap();
        bs.group(new NioEventLoopGroup()).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast("decode",new NettyDecoder())
                                .addLast("encode",new NettyEncoder())
                                .addLast(providerHandler);
                    }
                });
        return bs;
    }

    @Bean
    public ServerBootstrap serverBootstrap(){
        ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast("decode",new NettyDecoder())
                                    .addLast("encode",new NettyEncoder())
                                    .addLast(providerHandler);
                        }
                    });

        return serverBootstrap;
    }
    @Bean(name = "bossGroup")
    public EventLoopGroup BossGroup(){
        return new NioEventLoopGroup(1);
    }

    @Bean(name = "workerGroup")
    public EventLoopGroup WorkerGroup(){
        return new NioEventLoopGroup(12);
    }

}
