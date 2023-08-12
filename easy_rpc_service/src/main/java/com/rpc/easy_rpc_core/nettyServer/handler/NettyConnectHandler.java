package com.rpc.easy_rpc_core.nettyServer.handler;

import com.rpc.domain.protocol.enum2.RequestType;
import com.rpc.domain.rpc.CommonHeader;
import com.rpc.domain.rpc.RpcRequestHolder;
import com.rpc.domain.rpc.ServiceListHolder;
import com.rpc.domain.rpc.ServiceMeta;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@ChannelHandler.Sharable
public class NettyConnectHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    ServiceListHolder serviceListHolder;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg);
        ctx.fireChannelRead(msg);
//        RpcRequestHolder requestHolder = (RpcRequestHolder) msg;
//        RpcRequestHolder rpcRequestHolder = (RpcRequestHolder) msg;
//        CommonHeader commonHeader = rpcRequestHolder.getCommonHeader();
//        log.info("请求状态为"+commonHeader.getType());
////        添加服务请求
//        if (commonHeader.getType().equals(RequestType.PUT_SERVICE)) {
//            List<ServiceMeta> serviceMetas = (List<ServiceMeta>) rpcRequestHolder.getData();
//            for (ServiceMeta serviceMeta : serviceMetas) {
//                putService(serviceMeta);
//            }
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void putService(ServiceMeta serviceMeta){
        serviceListHolder.addService(serviceMeta);

    }

}
