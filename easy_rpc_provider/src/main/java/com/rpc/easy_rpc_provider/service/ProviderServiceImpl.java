package com.rpc.easy_rpc_provider.service;

import com.rpc.domain.cach.ConnectCache;
import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.rpc.*;
import com.rpc.domain.utils.SpringContextUtil;
import com.rpc.domain.protocol.enum2.RequestType;
import com.rpc.domain.rpc.HeartBeat;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ProviderServiceImpl implements ProviderService {

    @Resource
    Bootstrap bootstrap;

    @Resource
    RpcProperties.RPCServer server;

    @Resource
    ConnectCache connectCache;

    private List<ServiceMeta> serviceMetas = new ArrayList<>();


    @Override
    public Boolean registerService() {
//        首先尝试连接注册中心
        String host = server.getHost();
        int port = server.getPort();
        ChannelFuture channelFuture = connectTargetService(host, port);
        if (channelFuture != null){
            try {
                CommonHeader commonHeader = new CommonHeader(RequestType.SEND_SERVICE);
                RpcRequestHolder requestHolder = new RpcRequestHolder(commonHeader,serviceMetas);
                channelFuture.channel().writeAndFlush(requestHolder);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void addServiceMeta(ServiceMeta serviceMeta){
        this.serviceMetas.add(serviceMeta);
    }

    @Scheduled(cron="0/5 * *  * * ? ")
    @Override
    public void sendHeart() {
        CommonHeader header = new CommonHeader(RequestType.SEND_HEARTBEAT);
        HeartBeat heartBeat = new HeartBeat();
        RpcRequestHolder requestHolder = new RpcRequestHolder(header,heartBeat);
        ChannelFuture channelFuture = connectTargetService(server.getHost(), server.getPort());
        if(channelFuture!=null){
            try {
                channelFuture.channel().writeAndFlush(requestHolder).sync();
                log.info("心跳发送成功");
            }catch (Exception e){
                log.error("心跳发送失败");
                e.printStackTrace();
            }
        }else {
            log.error("与注册中心失去联系！心跳发送失败");
        }
    }

    /**
     * 核心！
     * 此方法实现响应数据，即执行服务提供者的业务代码然后返回给调用者
     * 步骤：
     * 1： 判断为消费服务请求,进行消费处理
     * 2： 解析 RpcRequestHolder, 拿出消费请求体中的参数
     * 3： 根据请求体中beanName来注入服务业务类并生成代理对象
     * 4： 执行代理方法返回结果，封装结果发送数据
     */
    @Override
    public  RpcRequestHolder responseConsume(ConsumeRequest consumeRequest) {
        // 提取消费请求中的参数，包括标识id，服务方beanName，方法名，参数类型，参数值
        ProviderResponse providerResponse = new ProviderResponse();
        CommonHeader header = new CommonHeader(RequestType.RESPONSE_SERVICE);
        String requestId = consumeRequest.getRequestId();
        providerResponse.setRequestId(requestId);
        String beanName = consumeRequest.getBeanName();
        String methodName = consumeRequest.getMethodName();
        Class<?>[] parameterTypes = consumeRequest.getParameterTypes();
        Object[] parameters = consumeRequest.getParameters();
        Object serviceBean = SpringContextUtil.getBeanByName(beanName);
        // 创建代理对象并执行目标方法，进行异常处理
        Class<?> serviceClass = serviceBean.getClass();
        FastClass fastClass = FastClass.create(serviceClass);
        int methodIndex = fastClass.getIndex(methodName, parameterTypes);
        try {
            Object targetMethodRes = fastClass.invoke(methodIndex, serviceBean, parameters);
            providerResponse.setCode(200);
            providerResponse.setIsSuccess(true);
            providerResponse.setResult(targetMethodRes);
            log.info("执行成功!");
        }catch (InvocationTargetException e ){
            e.printStackTrace();
            log.error("目标方法执行出错，检查方法:"+methodName+"执行beanName:"+beanName);
            providerResponse.setError("服务方运行出错！---执行beanName:"+beanName+"执行方法名:"+methodName);
            providerResponse.setCode(500);
            providerResponse.setIsSuccess(false);
        }
        return new RpcRequestHolder(header,providerResponse);
    }

    /**
     * 指定端口进行连接
     * @param host
     * @param port
     * @return
     */
    private ChannelFuture connectTargetService(String host,int port) {
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
            log.info("连接成功,目标Host:"+host+"port:"+port);
            return channelFuture;
        }else {
            return null;
        }
    }
}
