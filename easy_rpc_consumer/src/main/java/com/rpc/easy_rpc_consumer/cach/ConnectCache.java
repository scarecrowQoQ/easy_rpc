package com.rpc.easy_rpc_consumer.cach;

import io.netty.channel.ChannelFuture;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectCache {
    private final ConcurrentHashMap<String , ChannelFuture> connectCache = new ConcurrentHashMap<>();

    public ChannelFuture getChannelFuture(String address){
        if(connectCache.contains(address)){
            return connectCache.get(address);
        }else {
            return null;
        }
    }
    public void putChannelFuture(String address,ChannelFuture channelFuture){
        connectCache.put(address,channelFuture);
    }

    public void removeChannelFuture(String address){
        if(connectCache.contains(address)){
            connectCache.remove(address);
        }
    }
}
