package com.rpc.domain.protocol.bean;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Data
@Slf4j
public class ServiceListHolder implements Serializable {

//  当前缓存的服务列表， key=serviceName，value为当前机器下的所有服务元数据(没有线程安全问题)
    private final ConcurrentHashMap<String, List<ServiceMeta>> serviceList = new ConcurrentHashMap<>();

    /**
     * 获取指定服务进行服务调用
     * @param serviceName
     * @return
     */
    public List<ServiceMeta> getServiceByName(String serviceName){
        return serviceList.getOrDefault(serviceName, new ArrayList<>());
    }

    public boolean isExist(ServiceMeta var1){
        String serviceName = var1.getServiceName();
//      这里需要null判断，可能存在服务先启动注册中心后启动的情况，此时serviceListHolder可能没有添加过该服务
        List<ServiceMeta> ServiceMetas = serviceList.get(serviceName);
        if(ServiceMetas!=null){
            for (ServiceMeta serviceMeta : ServiceMetas) {
                if(var1.getServiceHost().equals(serviceMeta.getServiceHost()) && var1.getServicePort() == serviceMeta.getServicePort()){
                    return true;
                }
            }
        }
        return false;
    }
}
