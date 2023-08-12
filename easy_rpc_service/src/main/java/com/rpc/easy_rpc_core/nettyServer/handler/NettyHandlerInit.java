package com.rpc.easy_rpc_core.nettyServer.handler;

import com.rpc.domain.rpc.ServiceListHolder;
import com.rpc.domain.protocol.enum2.RequestType;
import com.rpc.domain.rpc.CommonHeader;
import com.rpc.domain.rpc.RpcRequestHolder;
import com.rpc.domain.rpc.ServiceMeta;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ChannelHandler.Sharable
public class NettyHandlerInit extends ChannelInboundHandlerAdapter {

    @Autowired
    ServiceListHolder serviceListHolder;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequestHolder requestHolder = (RpcRequestHolder) msg;
        System.out.println(requestHolder);
//        RpcRequestHolder rpcRequestHolder = (RpcRequestHolder) msg;
//        CommonHeader commonHeader = rpcRequestHolder.getCommonHeader();
//        System.out.println("发现添加的服务");
////        添加服务请求
//        if (commonHeader.getType().equals(RequestType.PUT_SERVICE)) {
//            List<ServiceMeta> serviceMetas = (List<ServiceMeta>) rpcRequestHolder.getData();
//            for (ServiceMeta serviceMeta : serviceMetas) {
//                putService(serviceMeta);
//            }
//        }
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接成功");
    }

    private void putService(ServiceMeta serviceMeta){
        serviceListHolder.addService(serviceMeta);
    }
}
