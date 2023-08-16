package com.rpc.domain.rpc;

import com.rpc.domain.rpc.ServiceMeta;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Data
@Slf4j
public class ServiceListHolder implements Serializable {

    private  ConcurrentHashMap<String, List<ServiceMeta>> serviceList = new ConcurrentHashMap<>();

    public void addService(List<ServiceMeta> metas){
        for (ServiceMeta meta : metas) {
            String serviceName = meta.getServiceName();
            List<ServiceMeta> serviceMetas = serviceList.getOrDefault(serviceName, new ArrayList<>());
            serviceMetas.add(meta);
            serviceList.put(serviceName,serviceMetas);
        }

    }

    public void updateService(ConcurrentHashMap<String, List<ServiceMeta>> newServiceList){
        this.serviceList = newServiceList;
    }

    /**
     * 获取指定服务进行服务调用！
     * 需要添加一个负载均衡器
     * @param serviceName
     * @return
     */
    public List<ServiceMeta>  getService(String serviceName){
        //            进行负载均衡策略（暂时返回第一个）
        return serviceList.getOrDefault(serviceName, null);

    }


}
