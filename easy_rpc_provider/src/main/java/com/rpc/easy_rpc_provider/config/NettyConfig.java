package com.rpc.easy_rpc_provider.config;

import com.rpc.easy_rpc_govern.config.RpcConfigProperties;
import com.rpc.easy_rpc_protocol.coder.NettyDecoder;
import com.rpc.easy_rpc_protocol.coder.NettyEncoder;
import com.rpc.easy_rpc_provider.nettyServer.handler.ProviderHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@Data
@Slf4j
public class NettyConfig {

    @Resource
    RpcConfigProperties.RpcProvider provider;

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


}
