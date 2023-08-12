package com.rpc.easy_rpc_provider.provider;

import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.rpc.ServiceMeta;
import com.rpc.easy_rpc_provider.annotation.EasyRpcProvider;
import com.rpc.easy_rpc_provider.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;

/**
 * 实现服务注册功能，
 */

@Configuration
@RequiredArgsConstructor
public class ProviderFinder implements BeanPostProcessor{
    @Autowired
    private RpcProperties.provider provider;

    @Resource
    ProviderService providerService;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        EasyRpcProvider easyRpcProvider = bean.getClass().getAnnotation(EasyRpcProvider.class);
        if (easyRpcProvider != null) {
            ServiceMeta serviceMeta = null;
            String serviceName = easyRpcProvider.serviceName();
            String serviceHost = provider.getHost();
            int servicePort = provider.getPort();
            serviceMeta = new ServiceMeta(serviceName, beanName, serviceHost, servicePort,System.currentTimeMillis());
            providerService.registerService(serviceMeta);
        }
        return bean;
    }
}
