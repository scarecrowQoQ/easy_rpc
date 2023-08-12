package com.rpc.easy_rpc_provider.nettyServer;

import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.protocol.coder.NettyDecoder;
import com.rpc.domain.protocol.coder.NettyEncoder;
import com.rpc.easy_rpc_provider.nettyServer.handler.ProviderHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.net.InetSocketAddress;

@Service
public class NettyServerStarter {

    @Resource
    @Qualifier("bossGroup")
    EventLoopGroup bossGroup;

    @Resource
    @Qualifier("workerGroup")
    EventLoopGroup workerGroup;

    @Resource
    RpcProperties.provider provider;

    @Resource
    NettyDecoder nettyDecoder;

    @Resource
    NettyEncoder nettyEncoder;

    @Resource
    ProviderHandler providerHandler;

    public void start() throws InterruptedException {
        System.out.println("开启服务");
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            System.out.println("port="+provider.port);
            serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(provider.port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast("decode",nettyDecoder)
                                .addLast("encode",nettyEncoder)
                                .addLast(providerHandler);
                    }
                });
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            System.out.println("开启netty服务:"+ channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        } finally{
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }
}
