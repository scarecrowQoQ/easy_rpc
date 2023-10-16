package com.rpc.easy_rpc_registry.nettyServer;

import com.rpc.easy_rpc_govern.config.RpcConfigProperties;
import com.rpc.easy_rpc_registry.cluster.bean.NodeContent;
import com.rpc.easy_rpc_registry.cluster.handler.ClusterHandler;
import com.rpc.easy_rpc_registry.cluster.service.ClusterService;
import com.rpc.easy_rpc_registry.nettyServer.handler.RegistryInBoundHandler;
import com.rpc.easy_rpc_protocol.coder.NettyDecoder;
import com.rpc.easy_rpc_protocol.coder.NettyEncoder;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
@ComponentScan({"com.rpc.easy_rpc_registry","com.rpc.easy_rpc_protocol","com.rpc.easy_rpc_govern"})
@EnableScheduling
@Slf4j
public class NettyServiceStart implements Runnable, InitializingBean {

    @Autowired
    RpcConfigProperties.RpcRegistry rpcServer;

    @Resource
    @Qualifier("taskExecutor")
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

//  对消费与提供者的网络处理器
    @Resource
    RegistryInBoundHandler registryHandler;

//  集群内部的网络处理器
    @Resource
    ClusterHandler clusterHandler;

    @Resource
    @Lazy
    ClusterService clusterService;

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
                                    .addLast("decode",new NettyDecoder())
                                    .addLast("encode",new NettyEncoder())
                                    .addLast(registryHandler)
                                    .addLast(clusterHandler);
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            log.info("开启netty服务:"+ channelFuture.channel().localAddress());
            if (rpcServer.getCluster()) {
                clusterService.initCluster();
            }
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
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
