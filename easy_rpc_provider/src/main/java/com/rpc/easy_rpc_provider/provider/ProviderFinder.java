package com.rpc.easy_rpc_provider.provider;

import com.rpc.easy_rpc_govern.config.RpcConfigProperties;
import com.rpc.domain.bean.ServiceMeta;
import com.rpc.domain.annotation.EasyRpcProvider;
import com.rpc.easy_rpc_provider.service.ProviderService;
import lombok.Data;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * 实现服务注册功能，
 */

@Configuration
@Data
@EnableScheduling
public class ProviderFinder implements BeanPostProcessor{

    @Resource
    RpcConfigProperties.RpcProvider provider;

    @Resource
    ProviderService providerService;

    @Resource
    @Qualifier("serviceBean")
    HashMap<String,Object> serviceBean;

    @Override
    public Object postProcessBeforeInitialization(Object bean,@NonNull String beanName) throws BeansException {
        EasyRpcProvider easyRpcProvider = bean.getClass().getAnnotation(EasyRpcProvider.class);
        if (easyRpcProvider != null) {
            String serviceName = easyRpcProvider.serviceName();
            String serviceHost = provider.getHost();
            int servicePort = provider.getPort();
            ServiceMeta serviceMeta = new ServiceMeta(serviceName, serviceHost, servicePort,System.currentTimeMillis(),0,easyRpcProvider.group());
            providerService.addServiceMeta(serviceMeta);

            serviceBean.put(serviceName,bean);
        }
        return bean;
    }
}
