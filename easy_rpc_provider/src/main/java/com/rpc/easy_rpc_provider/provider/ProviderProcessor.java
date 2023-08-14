package com.rpc.easy_rpc_provider.provider;

import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.utils.SpringContextUtil;
import com.rpc.easy_rpc_provider.nettyServer.NettyServerStarter;
import com.rpc.domain.rpc.ServiceMeta;
import com.rpc.easy_rpc_provider.service.ProviderService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供者进程，主要开启服务响应功能
 */
@ComponentScan("com.rpc")
public class ProviderProcessor implements InitializingBean, EnvironmentAware {

    @Resource
    NettyServerStarter nettyServerStarter;

    @Resource
    ProviderService providerService;

    private List<ServiceMeta> serviceMetas = new ArrayList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        nettyServerStarter.start();
        System.out.println("开启线程");
    }

    @Override
    public void setEnvironment(Environment environment) {
        providerService.registerService();
    }
}
