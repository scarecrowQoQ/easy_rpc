package com.rpc.easy_rpc_registry.cluster.config;

import com.rpc.easy_rpc_protocol.coder.NettyDecoder;
import com.rpc.easy_rpc_protocol.coder.NettyEncoder;
import com.rpc.easy_rpc_registry.cluster.bean.NodeContent;
import com.rpc.easy_rpc_registry.cluster.enumeration.NodeStatus;
import com.rpc.easy_rpc_registry.cluster.handler.NodeHandler;
import com.rpc.easy_rpc_registry.cluster.listener.NodeStatusListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;

@Configuration
public class ClusterConfig {

    @Resource
    @Lazy
    NodeHandler nodeHandler;

    @Resource
    @Lazy
    NodeStatusListener nodeStatusListener;

//    用于节点间通信
    @Bean("clusterBootStrap")
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
                    }
                });
        return bs;
    }
//  注入并初始化节点内容
    @Bean
    public NodeContent nodeContent(){
        NodeContent nodeContent = new NodeContent();
        nodeContent.setNodeStatus(NodeStatus.inception);
        nodeContent.setLeaderId(null);
        nodeContent.setTermId(0);
        nodeContent.setVoted(false);
        nodeContent.setNodeStatusListener(nodeStatusListener);
        return nodeContent;
    }
}
