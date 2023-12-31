package com.rpc.easy_rpc_provider.nettyServer;

import com.rpc.easy_rpc_govern.config.RpcConfigProperties;
import com.rpc.easy_rpc_protocol.coder.NettyDecoder;
import com.rpc.easy_rpc_protocol.coder.NettyEncoder;
import com.rpc.easy_rpc_provider.nettyServer.handler.ProviderHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

@Component
@Slf4j
public class NettyServerStarter extends Thread {

    @Resource
    RpcConfigProperties.RpcProvider provider;

    @Resource
    ProviderHandler providerHandler;

    @SneakyThrows
    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(16);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(provider.port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast("decode",new NettyDecoder())
                                    .addLast("encode",new NettyEncoder())
                                    .addLast(providerHandler);
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            log.info("开启netty服务:"+ channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        } finally{
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }
}
