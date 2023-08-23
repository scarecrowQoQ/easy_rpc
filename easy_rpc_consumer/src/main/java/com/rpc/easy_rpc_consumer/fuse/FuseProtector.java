package com.rpc.easy_rpc_consumer.fuse;

import com.rpc.domain.rpc.ServiceListHolder;
import com.rpc.domain.rpc.ServiceMeta;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Data
@Slf4j
public class FuseProtector {

    private ConcurrentHashMap<String, ServiceState> serviceStateCache = new ConcurrentHashMap<>();

    @Resource
    ServiceListHolder serviceListHolder;

    /**
     * 检查当前熔断状态，许可发送请求返回true，拦截返回false
     * @param serviceName
     * @return
     */
    public boolean checkService(String serviceName){
        ServiceState serviceState = serviceStateCache.get(serviceName);
        switch (serviceState.getFuseState()) {
            case CLOSE:
                return true;
            case FALL_OPEN:
                return false;
            case HALF_OPEN:
                return Math.random() > serviceState.getInterceptRate();
        }
        return true;
    }

    public void initCache(ConcurrentHashMap<String, List<ServiceMeta>> serviceList){
        for (String serviceName : serviceList.keySet()) {
            ServiceState serviceState = new ServiceState(serviceName);
                serviceStateCache.put(serviceName,serviceState);
        }
    }
    @Async
    public void increaseRequest(String serviceName){
        ServiceState serviceState = serviceStateCache.get(serviceName);
        serviceState.incrRequest();
    }

    public void increaseExcepts(String serviceName){
        ServiceState serviceState = serviceStateCache.get(serviceName);
        serviceState.incrExcepts();
    }

    @Scheduled(cron="0/10 * *  * * ? ")
    public void refreshCache(){
        for (String serviceName : serviceStateCache.keySet()) {
            serviceStateCache.put(serviceName,new ServiceState(serviceName));
        }
    }
}
