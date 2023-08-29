package com.rpc.easy_rpc_core.cach;

import com.rpc.domain.protocol.bean.ServiceMeta;
import io.netty.channel.Channel;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Data
public class ProviderConnectCache {
    /**
     * 这里保存的是每个连接下提供的所有服务列表
     * 需要注意的是这里的服务列表并不一定完全可用，也就是不会发送至消费者
     * 发送消费者的服务列表是ServiceListHolder类，这个类内部的所有服务都是定期检查过的服务
     * 因此，如果一个服务因为系统故障，服务暂停，线程崩溃等等导致服务不可用但是仍然还保持连接，该缓存不会剔除该服务列表
     * 原因在于当服务恢复时，开始发送心跳。我们去更新ServiceListHolder中的服务列表上传时间，但是ServiceListHolder中以及没有该服务了
     * 服务并不会主动去提交自己的服务列表，除非在服务启动时，所以我们需要利用心跳来获取服务。
     * 心跳包中并不包含服务提供者的服务列表，那如果判断服务是否被剔除了呢，我们就需要一个连接缓存ProviderConnectCache来存储
     * （先假设这个提供者的所有服务均已过期，即ServiceListHolder没有该服务列表）步骤就是当心跳发过来的时候，通过key拿到所有的服务列表
     * 然后去判断ServiceListHolder中服务是否还存在，过时则发送提交服务请求，获取服务列表，否则直接更新ServiceListHolder中服务列表时间
     */
    private final ConcurrentHashMap<Channel, List<ServiceMeta>> providerConnectCache = new ConcurrentHashMap<>();

}
