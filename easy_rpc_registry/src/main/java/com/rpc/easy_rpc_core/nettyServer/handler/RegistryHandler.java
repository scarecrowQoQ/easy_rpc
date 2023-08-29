package com.rpc.easy_rpc_core.nettyServer.handler;

import com.rpc.domain.protocol.bean.*;
import com.rpc.domain.protocol.enum2.RequestType;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
@ChannelHandler.Sharable
public class RegistryHandler extends ChannelInboundHandlerAdapter {
    
    @Resource
    ServiceListHolder serviceListHolder;



    private static final List<Channel>consumerChannels = new ArrayList<>();

    private static final HashMap<Channel,List<ServiceMeta>> providerChannels = new HashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        RpcRequestHolder rpcRequestHolder = (RpcRequestHolder) msg;
        CommonHeader commonHeader = rpcRequestHolder.getCommonHeader();
        RequestType type = commonHeader.getType();
//        添加服务请求
        if (type.equals(RequestType.SEND_SERVICE)) {
            List<ServiceMeta> serviceMetas = (List<ServiceMeta>) rpcRequestHolder.getData();
            log.info("serviceMetas="+serviceMetas);
            providerChannels.put(channel,serviceMetas);
            putService(serviceMetas);
//            进行服务数据发送
            CommonHeader header = new CommonHeader(RequestType.SEND_SERVICE);
            RpcRequestHolder res = new RpcRequestHolder(header,serviceListHolder);
            for (Channel consumerChannel : consumerChannels) {
                consumerChannel.writeAndFlush(res);
            }
        }
//        是否获取服务列表
        else if(type.equals(RequestType.GET_SERVICE)){
            log.info("返回服务列表");
            consumerChannels.add(channel);
            CommonHeader header = new CommonHeader(RequestType.SEND_SERVICE);
            RpcRequestHolder res = new RpcRequestHolder(header,serviceListHolder);
            ctx.channel().writeAndFlush(res);
        }
//        是否为发送心跳
        else if(type.equals(RequestType.SEND_HEARTBEAT)){
            HeartBeat heartBeat = (HeartBeat) rpcRequestHolder.getData();
            Long updateTime = heartBeat.getUpdateTime();
            List<ServiceMeta> serviceMetas = providerChannels.get(channel);
            serviceListHolder.updateServerTime(serviceMetas,updateTime);
//            查找服务
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
//      下线服务是否为提供者
        if(providerChannels.containsKey(channel)){
            log.info("发现提供者下线");
//            获取该机器的所有服务列表
            List<ServiceMeta> serviceMetas = providerChannels.get(channel);
//            从本地缓存中剔除该机器下的 所有服务
            serviceListHolder.removeOneService(serviceMetas);
//            所有消费者更新服务列表
            for (Channel consumerChannel : consumerChannels) {
                CommonHeader header = new CommonHeader(RequestType.SEND_SERVICE);
                RpcRequestHolder res = new RpcRequestHolder(header,serviceListHolder);
                consumerChannel.writeAndFlush(res);
            }
//            从服务提供者连接缓存中剔除
            providerChannels.remove(channel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    private void putService( List<ServiceMeta> serviceMetas){
        serviceListHolder.addService(serviceMetas);
    }
}
