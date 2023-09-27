package com.rpc.easy_rpc_registry.registry;

import com.rpc.domain.bean.ServiceMeta;
import io.netty.channel.Channel;

import java.util.List;

public interface RegistryService {
    /**
     * 周期性检查服务过期时间是否大于 RpcRegistry.serviceSaveTime
     */
    public void checkOverdueService();

    /**
     * 更新服务注册时间
     * 当服务发送心跳时触发该任务
     */
    public void updateServerTime(List<ServiceMeta> serviceMetas,long newTime);

    /**
     * 添加服务
     */
    public void registerService(Channel channel, Object data);

    /**
     * 返回服务列表
     */
    public void responseService(Channel channel);

    /**
     * 处理服务提供者发送的心跳
     *  需要注意的是发送心跳时可能没有注册服务，因此需要检查是否注册了服务，如果没有，则发送请求服务列表消息
     */
    public  void handleHeartBeat(Channel channel, Object data);

    /**
     * 剔除服务
     * 当机器下线时触发该方法
     */
    public void removeOneService(List<ServiceMeta> serviceMetas);

    /**
     * 服务断开连接处理
     * @param channel
     */
    public void channelInActive(Channel channel);


}
