package com.example.registry2;

import com.rpc.domain.bean.RequestHeader;
import com.rpc.domain.bean.RpcRequestHolder;
import com.rpc.domain.enumeration.RequestType;
import com.rpc.easy_rpc_govern.config.RpcConfigProperties;
import com.rpc.easy_rpc_protocol.cach.ConnectCache;
import com.rpc.easy_rpc_registry.cluster.bean.NodeContent;
import com.rpc.easy_rpc_registry.cluster.bean.VoteRequest;
import com.rpc.easy_rpc_registry.utils.ConnectUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.util.Date;

@SpringBootTest
class Registry2ApplicationTests {

    @Autowired
    @Qualifier("clusterBootStrap")
    Bootstrap bootstrap;

    @Resource
    @Lazy
    NodeContent nodeContent;

    @Resource
    RpcConfigProperties.RpcRegistry rpcRegistry;

    @Test
    void contextLoads() {
        ChannelFuture channelFuture = ConnectUtil.connect("127.0.0.1", 8344, bootstrap, new ConnectCache());
        VoteRequest voteRequest = new VoteRequest(rpcRegistry.host+":"+rpcRegistry.port,nodeContent.getTermId());
        RequestHeader requestHeader = new RequestHeader(RequestType.ASK_VOTE);
        RpcRequestHolder rpcRequestHolder = new RpcRequestHolder(requestHeader,voteRequest);
        channelFuture.channel().writeAndFlush(rpcRequestHolder);
    }

}
