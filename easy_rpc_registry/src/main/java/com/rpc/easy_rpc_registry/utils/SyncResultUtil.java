package com.rpc.easy_rpc_registry.utils;
import com.rpc.domain.bean.ProviderResponse;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class SyncResultUtil {

    private static final ConcurrentHashMap<String, Promise<Object>> responseCache = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, AtomicInteger> responseCount = new ConcurrentHashMap<>();

    private final Lock lock = new ReentrantLock();

    private final Condition condition = lock.newCondition();

    public static <T> T get(String requestId, Class<T> type){
        Promise<Object> promise = responseCache.get(requestId);
        if(promise!= null){
            try {
                Object result = promise.get();
                if(type.isInstance(result)){
                    return type.cast(result);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                if(e instanceof InterruptedException){
                    log.error("获取结果超时！");
                }else {
                    log.error("结果同步获取失败！");
                }
            }
        }
        return null;
    }

    public static <T> T get(String requestId, Class<T> type,Long waitInMs){
        Promise<Object> promise = responseCache.get(requestId);
        if(promise!= null){
            try {
                Object result = promise.get(waitInMs, TimeUnit.MILLISECONDS);
                if(type.isInstance(result)){
                    return type.cast(result);
                }
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                e.printStackTrace();
                if(e instanceof InterruptedException){
                    log.error("获取结果超时！");
                }else {
                    log.error("结果同步获取失败！");
                }
            }
        }
        return null;
    }

    public static void setResult(String requestId,Object result){
        Promise<Object> promise = new DefaultPromise<>(new DefaultEventLoop());
        promise.setSuccess(result);
        responseCache.put(requestId,promise);
    }





}
