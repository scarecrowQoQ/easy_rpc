package com.rpc.easy_rpc_consumer.netServer;

import com.rpc.domain.config.RpcProperties;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
@Component
public class NettyServerStarter implements Runnable {
    @Resource
    RpcProperties.RPCServer server;

    @Resource
    Bootstrap bootstrap;
    @Override
    public void run() {
        ChannelFuture connect = bootstrap.connect(server.getHost(), server.getPort());
        connect.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(!channelFuture.isSuccess()){
                    System.out.println("服务器连接失败");
                }else {
                    System.out.println("服务器连接成功");
                }
            }
        });
    }


}
