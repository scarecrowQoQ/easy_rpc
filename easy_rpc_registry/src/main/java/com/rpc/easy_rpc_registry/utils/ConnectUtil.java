package com.rpc.easy_rpc_registry.utils;

import com.rpc.easy_rpc_govern.utils.SpringContextUtil;
import com.rpc.easy_rpc_protocol.cach.ConnectCache;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConnectUtil  {


    public static ChannelFuture connect(String host, int port, Bootstrap bootstrap,ConnectCache connectCache){
        String address = host + ":" + port;
        ChannelFuture channelFuture = null;
        channelFuture = connectCache.getChannelFuture(address);
        if(channelFuture == null){
            try {
                channelFuture = bootstrap.connect(host, port).sync();
                connectCache.putChannelFuture(address,channelFuture);
            }catch (Exception e){
                log.error("连接失败，请检查服务端是否开启目标Host:"+host+"port:"+port);
                e.printStackTrace();
                return null;
            }
        }
        if (channelFuture!=null) {

//            log.info("连接成功,目标Host:"+host+"port:"+port);
            return channelFuture;
        }else {
            return null;
        }
    }


}
