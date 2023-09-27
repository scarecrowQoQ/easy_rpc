package com.rpc.easy_rpc_consumer.netServer.handler;

import com.rpc.easy_rpc_protocol.cach.ConnectCache;
import com.rpc.easy_rpc_govern.config.RpcConfigProperties;
import com.rpc.domain.bean.RpcServiceList;
import com.rpc.domain.bean.*;

import com.rpc.easy_rpc_consumer.cach.ResponseCache;
import com.rpc.easy_rpc_govern.context.RpcContext;
import com.rpc.easy_rpc_govern.fuse.FuseProtector;
import com.rpc.domain.enumeration.RequestType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
@Component
@Slf4j
public class ConsumerHandler extends ChannelInboundHandlerAdapter {

    @Resource
    RpcServiceList rpcServiceList;

    @Resource
    ResponseCache responseCache;

    @Resource
    FuseProtector fuseProtector;

    @Resource
    ConnectCache connectCache;

    @Resource
    Bootstrap bootstrap;


    @Resource
    RpcConfigProperties.RPCConsumer consumer;

    /**
     * 这里进行服务提供者返回数据接收
     * 将接受到的数据放入响应缓存中，方便在service层中获取响应数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequestHolder requestHolder = (RpcRequestHolder) msg;
        RequestHeader commonHeader = requestHolder.getRequestHeader();
        RequestType type = commonHeader.getType();
        String requestId = commonHeader.getRequestId();
        if(type.equals(RequestType.RESPONSE_SERVICE)){
            ProviderResponse providerResponse = (ProviderResponse) requestHolder.getData();
            Promise<ProviderResponse> promise = responseCache.getPromise(requestId);
            promise.setSuccess(providerResponse);
        }
//        服务列表获取
        else if(type.equals(RequestType.SEND_SERVICE)){
            RpcServiceList newRpcServiceList = (RpcServiceList) requestHolder.getData();
            log.info("成功获取到服务列表"+newRpcServiceList.toString());
            fuseProtector.initCache(newRpcServiceList.getServiceList());
            rpcServiceList.setServiceList(newRpcServiceList.getServiceList());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public Object sendRequest(){
        RpcContext rpcContext = RpcContext.getContext();

        String requestId = rpcContext.getRequestHeader().getRequestId();
        ServiceMeta remoteService = rpcContext.getRemoteService();
        ChannelFuture channelFuture = connectTargetService(remoteService.getServiceHost(), remoteService.getServicePort());
        if(channelFuture != null){
            Channel channel = channelFuture.channel();
            if (!channel.isActive()) {
                String address = remoteService.getServiceHost()+":"+remoteService.getServicePort();
                log.info("远程服务掉线！ 地址:"+address);
//                剔除缓存中的ChannelFuture
                connectCache.removeChannelFuture(address);
                return null;
            }
//            promise用于接下来同步接受数据，数据的接受在ConsumerHandler中，在handler中将数据放回promise中，这样就可以拿到数据了
            Promise<ProviderResponse> promise = new DefaultPromise<>(new DefaultEventLoop());
//            通过唯一的requestId 来确保handler存值的promise和这里的promise为同一个对象
            responseCache.putPromise(requestId,promise);
            try {
                RpcRequestHolder rpcRequestHolder = new RpcRequestHolder(rpcContext.getRequestHeader(),rpcContext.getConsumeRequest());
                channel.writeAndFlush(rpcRequestHolder);
                ProviderResponse result = promise.get(consumer.getConsumeWaitInMs(), TimeUnit.MILLISECONDS);
                fuseProtector.incrSuccess(remoteService.getServiceName());
//                移除当前数据返回值的缓存
                responseCache.removeResponse(requestId);
                return result.getResult();
            }catch (Exception e){
                fuseProtector.incrExcept(remoteService.getServiceName());
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 指定端口进行连接
     * @param host
     * @param port
     * @return
     */
    private ChannelFuture connectTargetService(String host, int port){
        String address = host + ":" + port;
        ChannelFuture channelFuture = null;
        channelFuture = connectCache.getChannelFuture(address);
        if(channelFuture == null){
            try {
                channelFuture = bootstrap.connect(host, port).sync();
            }catch (Exception e){
                log.error("连接失败，请检查服务端是否开启目标Host:"+host+"port:"+port);
                e.printStackTrace();
                return null;
            }
        }
        if (channelFuture!=null && channelFuture.isSuccess()) {
            connectCache.putChannelFuture(address,channelFuture);
//            log.info("连接成功,目标Host:"+host+"port:"+port);
            return channelFuture;
        }else {
            return null;
        }
    }
}
