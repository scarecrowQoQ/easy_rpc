package com.rpc.easy_rpc_provider.provider;

import com.rpc.easy_rpc_provider.nettyServer.NettyServerStarter;
import com.rpc.domain.protocol.bean.ServiceMeta;
import com.rpc.easy_rpc_provider.service.ProviderService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.ComponentScan;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供者进程，主要开启服务响应功能
 */
@ComponentScan({"com.rpc.easy_rpc_provider", "com.rpc.domain"})
public class ProviderProcessor implements InitializingBean {

    @Resource
    NettyServerStarter nettyServerStarter;

    @Resource
    ProviderService providerService;

    private List<ServiceMeta> serviceMetas = new ArrayList<>();

    @Override
    public void afterPropertiesSet(){
        nettyServerStarter.start();
        providerService.registerService();
    }


}
