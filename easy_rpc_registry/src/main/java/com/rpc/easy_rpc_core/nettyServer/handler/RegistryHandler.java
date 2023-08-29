package com.rpc.easy_rpc_core.nettyServer.handler;

import com.rpc.domain.protocol.bean.*;
import com.rpc.domain.protocol.enum2.RequestType;
import com.rpc.easy_rpc_core.cach.ConsumerConnectCache;
import com.rpc.easy_rpc_core.cach.ProviderConnectCache;
import com.rpc.easy_rpc_core.registry.RegistryService;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
@ChannelHandler.Sharable
public class RegistryHandler extends ChannelInboundHandlerAdapter {

    @Resource
    RegistryService registryService;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        RpcRequestHolder rpcRequestHolder = (RpcRequestHolder) msg;
        CommonHeader commonHeader = rpcRequestHolder.getCommonHeader();
        RequestType type = commonHeader.getType();
        Object data = rpcRequestHolder.getData();
//        添加服务请求
        if (type.equals(RequestType.SEND_SERVICE)) {
            registryService.registerService(channel,data);
        }
//        是否获取服务列表
        else if(type.equals(RequestType.GET_SERVICE)){
            registryService.responseService(channel);
        }
//        是否为发送心跳
        else if(type.equals(RequestType.SEND_HEARTBEAT)) {
            registryService.handleHeartBeat(channel,data);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        Channel channel = ctx.channel();
        registryService.channelInActive(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){

    }

}
