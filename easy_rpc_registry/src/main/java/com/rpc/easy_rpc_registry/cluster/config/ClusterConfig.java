package com.rpc.easy_rpc_registry.cluster.config;

import com.rpc.easy_rpc_govern.config.RpcConfigProperties;
import com.rpc.easy_rpc_protocol.cach.ConnectCache;
import com.rpc.easy_rpc_protocol.coder.NettyDecoder;
import com.rpc.easy_rpc_protocol.coder.NettyEncoder;
import com.rpc.easy_rpc_registry.cluster.bean.NodeContent;
import com.rpc.easy_rpc_registry.cluster.handler.NodeHandler;
import com.rpc.easy_rpc_registry.cluster.listener.NodeStatusListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ClusterConfig {

    @Resource
    @Lazy
    NodeHandler nodeHandler;

    @Resource
    NodeStatusListener nodeStatusListener;

//    @Resource
//    @Lazy
//    RegistryOutBoundHandler registryOutBoundHandler;

    @Resource
    RpcConfigProperties.RpcRegistry registry;



//    用于节点间通信
    @Bean("clusterBootStrap")
    @ConditionalOnProperty(name = "rpc.registry.cluster", havingValue = "true")
    @DependsOn({"nodeHandler"})
    public Bootstrap Bootstrap(){
        Bootstrap bs = new Bootstrap();
        bs.group(new NioEventLoopGroup()).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast("decode",new NettyDecoder())
                                .addLast("encode",new NettyEncoder())
                                .addLast(nodeHandler);
//                                .addLast(registryOutBoundHandler);
                    }
                });
        return bs;
    }

//  保存其他节点channel
    @Bean("clusterConnectCache")
    @ConditionalOnProperty(name = "rpc.registry.cluster", havingValue = "true")
    public ConnectCache connectCache(){
        return new ConnectCache();
    }

//  注入并初始化节点内容
    @Bean
    @ConditionalOnProperty(name = "rpc.registry.cluster", havingValue = "true")
    @DependsOn({"nodeStatusListener"})
    public NodeContent nodeContent(){
        NodeContent nodeContent = new NodeContent();
        nodeContent.setAddress(registry.host+":"+registry.port);
        nodeContent.setTermId(0);
        nodeContent.setLeaderAddress(null);
        nodeContent.setClusterNodeAmount(registry.getClusterAddress().size());
        nodeContent.setBallot(new AtomicInteger(0));
        nodeContent.setNodeStatusListener(nodeStatusListener);
        nodeContent.setVoted(false);
        nodeContent.setLeaderHeartBeat(null);
        return nodeContent;
    }


}
