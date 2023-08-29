package com.rpc.easy_rpc_core.registry;

import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.protocol.bean.*;
import com.rpc.domain.protocol.enum2.RequestType;
import com.rpc.easy_rpc_core.cach.ConsumerConnectCache;
import com.rpc.easy_rpc_core.cach.ProviderConnectCache;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class RegistryServiceImpl implements RegistryService{
    @Resource
    ServiceListHolder serviceListHolder;

    @Resource
    RpcProperties.RpcRegistry server;

    @Resource
    ConsumerConnectCache consumerConnectCache;

    @Resource
    ProviderConnectCache providerConnectCache;

    @Override
    @Scheduled(fixedDelayString  = "#{RpcRegistry.serviceSaveTime}")
    public void checkOverdueService() {
        ConcurrentHashMap<String, List<ServiceMeta>> serviceList = serviceListHolder.getServiceList();
        for (String serviceName : serviceList.keySet()) {
            List<ServiceMeta> serviceMetas = serviceList.get(serviceName);
            serviceMetas.removeIf(this::isOverdueService);
        }
    }

    @Override
    public void registerService(Channel channel,Object data){
        List<ServiceMeta> receiveServiceMetas = (List<ServiceMeta>) data;
        ConcurrentHashMap<Channel, List<ServiceMeta>> providerChannels = providerConnectCache.getProviderConnectCache();
        CopyOnWriteArrayList<Channel> consumerChannels = consumerConnectCache.getConnectCache();

//      此方法在一般在初次连接时触发，因此要添加至缓存中
        providerChannels.put(channel,receiveServiceMetas);

//      将所有接受的服务添加到本地的服务列表中，需要判重防止重复添加
        ConcurrentHashMap<String, List<ServiceMeta>> serviceList = serviceListHolder.getServiceList();
        for (ServiceMeta meta : receiveServiceMetas) {
            String serviceName = meta.getServiceName();
            List<ServiceMeta> serviceMetas = serviceList.getOrDefault(serviceName, new CopyOnWriteArrayList<>());
            if(!serviceMetas.contains(meta)){
                serviceMetas.add(meta);
            }

//          此步需要，如果serviceMetas为new出来的需要放入map中
            serviceList.put(serviceName,serviceMetas);

//          发送新的服务列表至所有消费者
            CommonHeader header = new CommonHeader(RequestType.SEND_SERVICE);
            RpcRequestHolder res = new RpcRequestHolder(header,serviceListHolder);
            for (Channel consumerChannel : consumerChannels) {
                consumerChannel.writeAndFlush(res);
            }
            log.info("添加了一个服务："+serviceName);
        }
    }
    @Override
    public void responseService(Channel channel) {
        CopyOnWriteArrayList<Channel> consumerChannels = consumerConnectCache.getConnectCache();
        if (!consumerChannels.contains(channel)) {
            consumerChannels.add(channel);
        }

        CommonHeader header = new CommonHeader(RequestType.SEND_SERVICE);
        RpcRequestHolder res = new RpcRequestHolder(header,serviceListHolder);
        channel.writeAndFlush(res);
    }

    @Override
    public void handleHeartBeat(Channel channel, Object data) {
        HeartBeat heartBeat = (HeartBeat) data;
        Long updateTime = heartBeat.getUpdateTime();
        ConcurrentHashMap<Channel, List<ServiceMeta>> ConnectCache = this.providerConnectCache.getProviderConnectCache();
        List<ServiceMeta> serviceMetas = ConnectCache.get(channel);
//        如果当前连接缓存中没有之前保存的数据，则直接进行服务列表获取
        if(serviceMetas == null){
            log.info("请求服务注册");
            CommonHeader header = new CommonHeader(RequestType.GET_SERVICE);
            RpcRequestHolder requestHolder = new RpcRequestHolder(header, null);
            channel.writeAndFlush(requestHolder);
            return;
        }
//      首先检查ServiceListHolder中该服务列表是否还在
        for (ServiceMeta serviceMeta : serviceMetas) {
//          如果有服务不存在(被剔除)则请求服务列表，心跳包并不包含服务列表
            if (!serviceListHolder.isExist(serviceMeta)){
                log.info("请求服务注册");
                CommonHeader header = new CommonHeader(RequestType.GET_SERVICE);
                RpcRequestHolder requestHolder = new RpcRequestHolder(header, null);
                channel.writeAndFlush(requestHolder);
                return;
            }
        }
        this.updateServerTime(serviceMetas, updateTime);
    }

    /**
     * 移除服务
     * 一个服务名对应的服务列表是集群，因此要考虑集群
     * 移除一台机器上所有的服务列表
     * @param serviceMetas
     */
    @Override
    public void removeOneService(List<ServiceMeta> serviceMetas){
        ConcurrentHashMap<String, List<ServiceMeta>> serviceList = serviceListHolder.getServiceList();
//        将该机器的所有服务进行遍历
        for (ServiceMeta serviceMeta : serviceMetas) {
//          获取服务名
            String serviceName = serviceMeta.getServiceName();
//          获取该服务名下缓存的服务集群元数据
            List<ServiceMeta> curServiceMetas = serviceList.get(serviceName);
//          如果该服务只有一台机器
            if(curServiceMetas.size() == 1){
//          直接剔除该服务
                serviceList.remove(serviceName);
            }else {
//          如果不止一台机器则去除一个ServiceMeta，但仍保留该服务
                curServiceMetas.remove(serviceMeta);
            }
        }
    }

    /**
     *
     * 一个服务名对应的服务列表是集群，因此要考虑集群
     * 更新一台机器的所有服务列表更新时间
     * 需要注意的是这里更新的列表是serviceListHolder中的服务列表，而不是providerConnectCache中的列表
     * @param serviceMetas
     */
    @Override
    public void updateServerTime(List<ServiceMeta> serviceMetas,long newTime){
        ConcurrentHashMap<String, List<ServiceMeta>> serviceList = serviceListHolder.getServiceList();
//           将该机器的所有服务进行遍历
        for (ServiceMeta serviceMeta : serviceMetas) {
            String serviceName = serviceMeta.getServiceName();
//            获取该服务名下缓存的服务集群元数据
            List<ServiceMeta> curServiceMetas = serviceList.get(serviceName);
            int index = curServiceMetas.indexOf(serviceMeta);
            if(index != -1){
//              更新时间并保存
                serviceMeta.setUpdateTime(newTime);
                curServiceMetas.set(index,serviceMeta);
            }
        }
    }

    @Override
    public void channelInActive(Channel channel)  {
        CopyOnWriteArrayList<Channel> consumerChannels = consumerConnectCache.getConnectCache();
        ConcurrentHashMap<Channel, List<ServiceMeta>> providerChannels = this.providerConnectCache.getProviderConnectCache();
//      下线服务是否为提供者
        if(providerChannels.containsKey(channel)){
            log.info("发现提供者下线");
//            获取该机器的所有服务列表
            List<ServiceMeta> serviceMetas = providerChannels.get(channel);
//            从本地缓存中剔除该机器下的 所有服务
            this.removeOneService(serviceMetas);
//            所有消费者更新服务列表
            for (Channel consumerChannel : consumerChannels) {
                CommonHeader header = new CommonHeader(RequestType.SEND_SERVICE);
                RpcRequestHolder res = new RpcRequestHolder(header,serviceListHolder);
                consumerChannel.writeAndFlush(res);
            }
//            从服务提供者缓存中剔除
            providerChannels.remove(channel);
//            下线者是服务消费者
        }else {
            consumerChannels.remove(channel);
        }
    }

    private boolean isOverdueService(ServiceMeta serviceMeta){
        long IntervalTime = server.serviceSaveTime;
        long lastUpdateTime = serviceMeta.getUpdateTime();
        long now = System.currentTimeMillis();
        if ((now - lastUpdateTime) > IntervalTime){
            log.info("剔除服务："+serviceMeta.getServiceName());
        }
        return (now - lastUpdateTime) > IntervalTime;
    }
}
