package com.rpc.easy_rpc_protocol.cach;

import io.netty.channel.ChannelFuture;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectCache {

    private final ConcurrentHashMap<String , ChannelFuture> connectCache = new ConcurrentHashMap<>();

    public ChannelFuture getChannelFuture(String address){
        return connectCache.get(address);
    }
    public void putChannelFuture(String address,ChannelFuture channelFuture){
        connectCache.put(address,channelFuture);
    }

    public void removeChannelFuture(String address){
        connectCache.remove(address);
    }
}
