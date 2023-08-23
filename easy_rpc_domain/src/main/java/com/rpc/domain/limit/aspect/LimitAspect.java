package com.rpc.domain.limit.aspect;

import com.rpc.domain.limit.LimitProcess;
import com.rpc.domain.limit.entity.LimitingRule;
import com.rpc.domain.limit.handler.SelectLimitKey;
import com.rpc.domain.limit.limitAnnotation.LimitingStrategy;
import com.rpc.domain.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Aspect
@Service
@Slf4j
public class LimitAspect {
    @Resource
    LimitProcess limitProcess;

    @Pointcut("@annotation(com.rpc.domain.limit.limitAnnotation.LimitingStrategy)")
    private void annotationPointCut() {
    }

    @Around("annotationPointCut()")
    public Object annotationAround(ProceedingJoinPoint jp) {
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        LimitingStrategy limitingStrategy = method.getAnnotation(LimitingStrategy.class);
        String ruleName = limitingStrategy.strategyName();
        LimitingRule rule = SpringContextUtil.getBean(ruleName, LimitingRule.class);
        String limitKeyName = limitingStrategy.limitKey();
        float curQPS = limitProcess.getCurQPS();
        Object[] args = jp.getArgs();
        for (Object arg : args) {
            for (Field declaredField : arg.getClass().getDeclaredFields()) {
                if(declaredField.getName().equals(limitKeyName)){
                    try {
                        declaredField.setAccessible(true);
//                        SelectLimitKey selectLimitKey = rule.getSelectLimitKey();
                        Object key = declaredField.get(arg);
                        boolean pass = limitProcess.isPass(rule, key);
                        if(pass){
                            log.info("当前QPS="+curQPS+"，最大值"+rule.getQPS());
                            return jp.proceed();
                        }else {
                            log.warn("该请求被拦截，准备进行降级处理。当前QPS="+curQPS);
                            System.out.println("执行拦截");

                            return null;
                        }

                    } catch (Throwable e ){
                        e.printStackTrace();
                    }
                }
            }
        }
        log.error("没有找到拦截key，进行api粒度QPS拦截");
        boolean pass = limitProcess.isPass(rule);
        if(pass){
            try {
                return jp.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }else {
            log.info("已拦截该请求，当前QPS="+curQPS);
            return null;
        }
        return null;
    }
}
