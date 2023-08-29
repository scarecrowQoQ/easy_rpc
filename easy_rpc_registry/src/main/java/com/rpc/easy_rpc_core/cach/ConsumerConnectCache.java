package com.rpc.easy_rpc_core.cach;

import io.netty.channel.Channel;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArrayList;
@Component
@Data
public class ConsumerConnectCache {
    private final CopyOnWriteArrayList<Channel> connectCache = new CopyOnWriteArrayList<>();

}
