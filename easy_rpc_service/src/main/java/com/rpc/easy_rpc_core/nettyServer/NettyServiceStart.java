package com.rpc.easy_rpc_core.nettyServer;

import com.rpc.domain.config.RpcProperties;
import com.rpc.easy_rpc_core.nettyServer.handler.NettyHandlerInit;
import com.rpc.domain.protocol.coder.NettyDecoder;
import com.rpc.domain.protocol.coder.NettyEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
@ComponentScan("com.rpc.easy_rpc_core")
@Slf4j
public class NettyServiceStart implements Runnable, InitializingBean {

    @Autowired
    RpcProperties.RPCServer rpcServer;

    @Resource
    NettyHandlerInit httpHandlerInit;

    @Resource()
    @Qualifier("taskExecutor")
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    NettyHandlerInit nettyHandlerInit;

    @Resource
    NettyDecoder nettyDecoder;

    @Resource
    NettyEncoder nettyEncoder;

    @SneakyThrows
    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup(12);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(rpcServer.getPort()))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
//                                    .addLast("decode",new NettyDecoder())
//                                    .addLast("encode",new NettyEncoder())
                                    .addLast("decode",nettyDecoder)
                                    .addLast("encode",nettyEncoder)
                                    .addLast(nettyHandlerInit);
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            log.info("开启netty服务:"+ channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully().sync();
            workGroup.shutdownGracefully().sync();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        threadPoolTaskExecutor.execute(this);
    }
}
