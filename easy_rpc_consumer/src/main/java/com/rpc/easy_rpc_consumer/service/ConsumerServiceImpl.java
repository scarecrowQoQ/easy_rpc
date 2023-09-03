package com.rpc.easy_rpc_consumer.service;
import com.rpc.domain.protocol.bean.*;
import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.protocol.enum2.RequestType;
import com.rpc.domain.cach.ConnectCache;
import com.rpc.easy_rpc_consumer.cach.ResponseCache;
import com.rpc.easy_rpc_consumer.fuse.FuseProtector;
import com.rpc.easy_rpc_consumer.loadBalancer.LoadBalancer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class ConsumerServiceImpl implements ConsumerService{
    @Resource
    Bootstrap bootstrap;

    @Resource
    RpcProperties.RpcRegistry registry;

    @Resource
    RpcProperties.RPCConsumer consumer;

    @Resource
    ServiceListHolder serviceListHolder;

    @Resource
    ResponseCache responseCache;

    @Resource
    LoadBalancer loadBalancer;

    @Resource
    ConnectCache connectCache;

    @Resource
    FuseProtector fuseProtector;


    /**
     * 执行逻辑
     * 1: 根据请求的服务类型进行熔断判断
     * 2：根据负载均衡获取服务提供者元数据
     * 3: 根据元数据完善请求数据
     * 4：建立连接，封装RpcRequestHolder，发送请求
     * 5：如果在规定时间内返回则正常处理，添加一次正常请求次数
     * 6：如果超时则记录一次超时请求次数
     * @param consumeRequest
     * @return
     */
    @Override
    public Object sendRequest(ConsumeRequest consumeRequest, boolean async) {
        String serviceName = consumeRequest.getServiceName();
        /**
         * 检查服务状态，是否进行熔断
         */
        boolean isAvailable = fuseProtector.checkService(serviceName);
        if(!isAvailable){
            log.info("请求熔断");
            return null;
        }
        /**
         * 检测服务列表是否有该服务
         */
        List<ServiceMeta> services = serviceListHolder.getServiceByName(consumeRequest.getServiceName());
        if(services.size() == 0){
            log.error("没有可用服务!");
            return null;
        }
//        负载均衡选择
        ServiceMeta service = loadBalancer.selectService(services);
        String beanName = service.getBeanName();
//        填充beanName，这一步需要获取到服务提供者的数据才能填充
        consumeRequest.setBeanName(beanName);
        CommonHeader header = new CommonHeader(RequestType.CONSUME_SERVICE);
        String requestId = header.getRequestId();
//        进行连接
        String providerHost = service.getServiceHost();
        int providerPort = service.getServicePort();
        ChannelFuture channelFuture = connectTargetService(providerHost, providerPort);
        if(channelFuture != null){
            Channel channel = channelFuture.channel();
            if (!channel.isActive()) {
                log.info("远程服务掉线！ 地址"+providerHost+":"+providerHost);
//                剔除缓存中的ChannelFuture
                connectCache.removeChannelFuture(providerHost+":"+providerHost);
                return null;
            }
//            promise用于接下来同步接受数据，数据的接受在ConsumerHandler中，在handler中将数据放回promise中，这样就可以拿到数据了
            Promise<ProviderResponse> promise = new DefaultPromise<>(new DefaultEventLoop());
//            通过唯一的requestId 来确保handler存值的promise和这里的promise为同一个对象
            responseCache.putPromise(requestId,promise);
            RpcRequestHolder requestHolder = new RpcRequestHolder(header,consumeRequest);
            try {
                if(async){
                    return CompletableFuture.supplyAsync(()->{
                        channel.writeAndFlush(requestHolder);
                        try {
                            ProviderResponse result = promise.get(consumer.getConsumeWaitInMs(), TimeUnit.MILLISECONDS);
                            Object res = result.getResult();
                            return res;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    });

                }
//                消费发送
                channel.writeAndFlush(requestHolder);
//                同步获取返回值并设定超时时长
                ProviderResponse result = promise.get(consumer.getConsumeWaitInMs(), TimeUnit.MILLISECONDS);
//                移除当前数据返回值的缓存
                responseCache.removeResponse(requestId);
//                增加请求通过的次数
                fuseProtector.increaseRequest(serviceName);
                return result.getResult();
            }catch (Exception e){
//                如果当前异常为超时异常，则在熔断器中添加一次异常次数
                if(e instanceof TimeoutException){
                    log.info("连接超时");
                    fuseProtector.increaseExcepts(serviceName);
                }else {
                    log.error("消费发送失败！");
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     *  进行服务列表获取
     * @return
     */
    @Override
    public ServiceListHolder getServiceList() {
        CommonHeader header = new CommonHeader(RequestType.GET_SERVICE);
        RpcRequestHolder requestHolder = new RpcRequestHolder(header,null);
        ChannelFuture channelFuture = connectTargetService(registry.getHost(), registry.getPort());
        if(channelFuture != null){
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(requestHolder);
        }
        return null;
    }

    /**
     * 指定端口进行连接
     * @param host
     * @param port
     * @return
     */
    private ChannelFuture connectTargetService(String host,int port){
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
