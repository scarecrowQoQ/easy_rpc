package com.rpc.easy_rpc_consumer.netServer.handler;

import com.rpc.domain.protocol.enum2.RequestType;
import com.rpc.domain.rpc.CommonHeader;
import com.rpc.domain.rpc.ProviderResponse;
import com.rpc.easy_rpc_consumer.cach.ResponseCache;
import com.rpc.domain.rpc.ServiceListHolder;
import com.rpc.easy_rpc_consumer.fuse.FuseProtector;
import com.rpc.easy_rpc_consumer.service.ConsumerService;
import com.rpc.domain.rpc.RpcRequestHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@ChannelHandler.Sharable
@Component
@Slf4j
public class ConsumerHandler extends ChannelInboundHandlerAdapter {

    @Resource
    ServiceListHolder serviceListHolder;

    @Resource
    ResponseCache responseCache;

    @Resource
    FuseProtector fuseProtector;

    /**
     * 这里进行服务提供者返回数据接收
     * 将接受到的数据放入响应缓存中，方便在service层中获取响应数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequestHolder requestHolder = (RpcRequestHolder) msg;
        CommonHeader commonHeader = requestHolder.getCommonHeader();

        RequestType type = commonHeader.getType();
        if(type.equals(RequestType.RESPONSE_SERVICE)){
            ProviderResponse providerResponse = (ProviderResponse) requestHolder.getData();
            String requestId = providerResponse.getRequestId();
            Promise<ProviderResponse> promise = responseCache.getPromise(requestId);
            promise.setSuccess(providerResponse);
        }
//        服务列表获取
        else if(type.equals(RequestType.SEND_SERVICE)){
            ServiceListHolder newServiceListHolder = (ServiceListHolder) requestHolder.getData();
            log.info("成功获取到服务列表"+newServiceListHolder.toString());
            fuseProtector.initCache(newServiceListHolder.getServiceList());
            serviceListHolder.updateService(newServiceListHolder.getServiceList());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
