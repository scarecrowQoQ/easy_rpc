package com.rpc.easy_rpc_consumer.config;

import com.rpc.easy_rpc_consumer.netServer.handler.ConsumerHandler;
import com.rpc.domain.protocol.coder.NettyDecoder;
import com.rpc.domain.protocol.coder.NettyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;


@Configuration
public class NettyConfig {
    @Resource
    NettyDecoder nettyDecoder;
    @Resource
    NettyEncoder nettyEncoder;
    @Bean
    public Bootstrap Bootstrap(){
        Bootstrap bs = new Bootstrap();
        bs.group(new NioEventLoopGroup()).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast("decode", nettyDecoder)
                                .addLast("encode", nettyEncoder)
                                .addLast(new ConsumerHandler());
                    }
                });
        return bs;
    }
}
