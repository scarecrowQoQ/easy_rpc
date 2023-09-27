package com.rpc.easy_rpc_govern.limit.aspect;

import com.rpc.easy_rpc_govern.limit.LimitProcess;
import com.rpc.easy_rpc_govern.limit.entity.LimitingRule;
import com.rpc.easy_rpc_govern.limit.limitAnnotation.LimitingStrategy;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


@Service
@Slf4j
public class LimitAspect {
//    @Resource
//    LimitProcess limitProcess;
//
//    @Pointcut("@annotation(com.rpc.easy_rpc_govern.limit.limitAnnotation.LimitingStrategy)")
//    private void annotationPointCut() {
//    }
//
//    @Around("annotationPointCut()")
//    public Object annotationAround(ProceedingJoinPoint jp) {
//        Method method = ((MethodSignature) jp.getSignature()).getMethod();
//        LimitingStrategy limitingStrategy = method.getAnnotation(LimitingStrategy.class);
//        LimitingRule rule = new LimitingRule(limitingStrategy,jp.getArgs());
//        String limitKeyName = limitingStrategy.limitKey();
//        Object[] args = jp.getArgs();
////        如果设置了limitKey则查找属性名
//        if(!rule.getLimitKey().equals("")){
//            for (Object arg : args) {
//                for (Field declaredField : arg.getClass().getDeclaredFields()) {
//                    if(declaredField.getName().equals(limitKeyName)){
//                        try {
////                            设置可访问性
//                            declaredField.setAccessible(true);
////                            拿到值
//                            Object value = declaredField.get(arg);
//                            rule.setLimitValue(value);
//                            boolean pass = limitProcess.isPass(rule);
//                            if(pass){
//                                return jp.proceed();
//                            }else {
//                                return null;
//                            }
//                        } catch (Throwable e ){
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
//        boolean pass = limitProcess.isPass(rule);
//        if(pass){
//            try {
//                return jp.proceed();
//            } catch (Throwable throwable) {
//                throwable.printStackTrace();
//            }
//        }else {
//            log.info("已拦截");
//            return null;
//        }
//        return null;
//    }
}
