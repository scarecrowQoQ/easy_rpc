package com.rpc.easy_rpc_consumer.consumer;

import com.rpc.easy_rpc_consumer.annotation.RpcConsumer;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
@Configuration
public class ConsumerFinder implements BeanPostProcessor {
    @SneakyThrows
    @Override
    /**
     * 这个方法需要实现BeanPostProcessor接口
     * 该接口是一个bean在实例化之前会执行的方法
     * 需要注意的是 该方法会被所有被spring管理的bean都执行一边，所有你可以发现代码里面没有循环
     * 接口参数就是将要实例化的bean，和对于的beanName
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
//        这里遍历bean的属性，找到被特点注解标注的属性（我这里的是@RpcConsumer）
        for (Field declaredField : beanClass.getDeclaredFields()) {
            if(declaredField.isAnnotationPresent(RpcConsumer.class)){
//              拿去属性的值，这里根据业务需要
                final RpcConsumer rpcConsumer = declaredField.getAnnotation(RpcConsumer.class);
//              需要注意这里是getType而不是getClass，区别在于getType是获取（类）字段的类型的字节码，而getClass是获取运行时的对象的字节码
                Class<?> type = declaredField.getType();
                try {
//                  这里关闭java对该Field的类型检查，也是可访问性，若为不修改则接下来的set无法进行属性替换
                    declaredField.setAccessible(true);
//                  生成代理对象，根据自己的业务生成，需要注意的是new Class<?>[]{type}这个必须是个接口类型，也就是说被注解标注的属性必须是一个接口（jdk动态代理限制），
//                  最后参数就是自己的代理对象，需要实现InvocationHandler接口
                    Object proxy = Proxy.newProxyInstance(type.getClassLoader(),new Class<?>[]{type},new ConsumerProxy(rpcConsumer));
//                  最关键的一步，意思是将该bean的Field 也就是该属性设置为proxy，也就是第二个参数
                    declaredField.set(bean,proxy);
//                  恢复检查
                    declaredField.setAccessible(false);
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("生成代理对象失败，请检查是否为接口类型");
                }
            }
        }
//        需要注意的是，整个过程并没有对bean进行任何修改，只是对其指定的属性进行了替换，就比如我这里替换成了一个代理对象，
//        那么在实际中调用被 @RpcConsumer 标注的属性的方法时，会执行proxy代理对象的invoke方法，
//        即使这个属性是个接口并且没有实现类，也能完成业务方法（即该属性已经被替换成了代理对象）
        return bean;
    }
}
