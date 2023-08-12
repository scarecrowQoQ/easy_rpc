package com.rpc.easy_rpc_consumer.consumer;

import com.rpc.easy_rpc_consumer.annotation.RpcConsumer;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

@Configuration
@ComponentScan("com.rpc")
public class ConsumerProcessor implements InitializingBean, BeanPostProcessor {

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("连接注册中心");
    }

    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        for (Field declaredField : beanClass.getDeclaredFields()) {
          if(declaredField.isAnnotationPresent(RpcConsumer.class)){
              final RpcConsumer rpcConsumer = declaredField.getAnnotation(RpcConsumer.class);
              Class<?> type = declaredField.getType();
              try {
                  declaredField.setAccessible(true);
                  Object proxy = Proxy.newProxyInstance(type.getClassLoader(),new Class<?>[]{type},new ConsumerProxy(rpcConsumer));
                  declaredField.set(bean,proxy);
                  declaredField.setAccessible(false);
              }catch (Exception e){
                  e.printStackTrace();
                  System.out.println("生成代理对象失败，请检查是否为接口类型");
              }
          }
        }
        return bean;
    }
}
