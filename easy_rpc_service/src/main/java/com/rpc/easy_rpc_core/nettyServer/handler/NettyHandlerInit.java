package com.rpc.easy_rpc_core.nettyServer.handler;

import com.rpc.domain.protocol.enum2.RequestType;
import com.rpc.domain.rpc.CommonHeader;
import com.rpc.domain.rpc.ServiceListHolder;
import com.rpc.domain.rpc.RpcRequestHolder;
import com.rpc.domain.rpc.ServiceMeta;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@ChannelHandler.Sharable
public class NettyHandlerInit extends SimpleChannelInboundHandler<RpcRequestHolder> {
    
    @Autowired
    ServiceListHolder serviceListHolder;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequestHolder requestHolder = (RpcRequestHolder) msg;
        RpcRequestHolder rpcRequestHolder = (RpcRequestHolder) msg;
        CommonHeader commonHeader = rpcRequestHolder.getCommonHeader();
        RequestType type = commonHeader.getType();
        log.info("请求类型"+type);
//        添加服务请求
        if (type.equals(RequestType.SEND_SERVICE)) {
            List<ServiceMeta>  serviceMetas = ( List<ServiceMeta>) rpcRequestHolder.getData();
            putService(serviceMetas);
            log.info("添加服务："+String.valueOf(serviceMetas));
        }
//        是否获取服务列表
        if(type.equals(RequestType.GET_SERVICE)){
            log.info("返回服务列表");
            CommonHeader header = new CommonHeader(RequestType.SEND_SERVICE);
            RpcRequestHolder res = new RpcRequestHolder(header,serviceListHolder);
            ctx.channel().writeAndFlush(res);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequestHolder requestHolder) throws Exception {
        System.out.println(requestHolder);
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
