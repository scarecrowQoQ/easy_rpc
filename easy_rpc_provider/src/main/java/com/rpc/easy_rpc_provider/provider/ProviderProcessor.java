package com.rpc.easy_rpc_provider.provider;

import com.rpc.easy_rpc_provider.nettyServer.NettyServerStarter;
import com.rpc.domain.rpc.ServiceMeta;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.ComponentScan;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供者进程，主要开启服务响应功能
 */
@ComponentScan("com.rpc")
public class ProviderProcessor implements InitializingBean{

    @Resource
    NettyServerStarter nettyServerStarter;

    private List<ServiceMeta> serviceMetas = new ArrayList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        nettyServerStarter.start();
        System.out.println("afterPropertiesSet");
    }
}
