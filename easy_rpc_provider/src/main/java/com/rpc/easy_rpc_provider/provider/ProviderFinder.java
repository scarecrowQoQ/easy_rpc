package com.rpc.easy_rpc_provider.provider;

import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.rpc.ServiceMeta;
import com.rpc.easy_rpc_provider.annotation.EasyRpcProvider;
import com.rpc.easy_rpc_provider.service.ProviderService;
import lombok.Data;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 实现服务注册功能，
 */

@Configuration
@Data
@EnableScheduling
public class ProviderFinder implements BeanPostProcessor{

    @Resource
    RpcProperties.provider provider;

    @Resource
    ProviderService providerService;

    @Override
    public Object postProcessBeforeInitialization(Object bean,@NonNull String beanName) throws BeansException {
        EasyRpcProvider easyRpcProvider = bean.getClass().getAnnotation(EasyRpcProvider.class);
        if (easyRpcProvider != null) {
            String serviceName = easyRpcProvider.serviceName();
            String serviceHost = provider.getHost();
            int servicePort = provider.getPort();
            ServiceMeta serviceMeta = new ServiceMeta(serviceName, beanName, serviceHost, servicePort,System.currentTimeMillis(),0);
            providerService.addServiceMeta(serviceMeta);
        }
        return bean;
    }
}
