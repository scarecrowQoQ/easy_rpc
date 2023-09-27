package com.rpc.easy_rpc_provider.provider;

import com.rpc.easy_rpc_provider.nettyServer.NettyServerStarter;
import com.rpc.domain.bean.ServiceMeta;
import com.rpc.easy_rpc_provider.service.ProviderService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.ComponentScan;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供者进程，主要开启服务响应功能
 */
@ComponentScan({"com.rpc.easy_rpc_provider", "com.rpc.easy_rpc_govern", "com.rpc.easy_rpc_protocol"})
public class ProviderProcessor implements InitializingBean {

    @Resource
    NettyServerStarter nettyServerStarter;

    @Resource
    ProviderService providerService;

    @Override
    public void afterPropertiesSet(){
        nettyServerStarter.start();
        providerService.registerService();
    }


}
