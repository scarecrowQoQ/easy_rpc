package com.rpc.easy_rpc_registry.nettyServer.handler;

import com.rpc.domain.bean.HeartBeat;
import com.rpc.domain.bean.RequestHeader;
import com.rpc.domain.bean.RpcRequestHolder;
import com.rpc.easy_rpc_registry.registry.RegistryService;
import com.rpc.domain.enumeration.RequestType;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
@ChannelHandler.Sharable
public class RegistryInBoundHandler extends ChannelInboundHandlerAdapter {

    @Resource
    RegistryService registryService;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        RpcRequestHolder rpcRequestHolder = (RpcRequestHolder) msg;
        RequestHeader requestHeader = rpcRequestHolder.getRequestHeader();
        RequestType type = requestHeader.getType();
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
            HeartBeat heartBeat = (HeartBeat) data;
            registryService.handleHeartBeat(channel,heartBeat);
        }

//       如果都没有匹配则调用下一个处理器
        else {
            ctx.fireChannelRead(msg);
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
