package com.rpc.easy_rpc_core.registry;

import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.rpc.ServiceListHolder;
import com.rpc.domain.rpc.ServiceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
@Service
@Slf4j
public class RegistryServiceImpl implements RegistryService{
    @Resource
    ServiceListHolder serviceListHolder;

    @Resource
    RpcProperties.RPCServer server;

    @Override
    @Scheduled(cron="0/10 * *  * * ? ")
    public void updateServiceList() {
        int serviceSaveTime = server.getServiceSaveTime();
        long IntervalTime = serviceSaveTime* 1000L;
        ConcurrentHashMap<String, List<ServiceMeta>> serviceList = serviceListHolder.getServiceList();
        Collection<List<ServiceMeta>> values =  serviceList.values();
        for (List<ServiceMeta> serviceMetas : values) {
            serviceMetas.removeIf(serviceMeta -> isOverdueService(serviceMeta, IntervalTime));
        }
    }

    private boolean isOverdueService(ServiceMeta serviceMeta,long IntervalTime){
        long lastUpdateTime = serviceMeta.getUpdateTime();
        long now = System.currentTimeMillis();
        log.info("当前间隔="+(now-lastUpdateTime));
        return (now - lastUpdateTime) > IntervalTime;
    }
}
