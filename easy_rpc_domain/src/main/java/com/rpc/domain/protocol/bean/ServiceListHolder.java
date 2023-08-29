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
    private  ConcurrentHashMap<String, List<ServiceMeta>> serviceList = new ConcurrentHashMap<>();

    public void addService(List<ServiceMeta> metas){
        for (ServiceMeta meta : metas) {
            String serviceName = meta.getServiceName();
            List<ServiceMeta> serviceMetas = serviceList.getOrDefault(serviceName, new ArrayList<>());
            if(!serviceMetas.contains(meta)){
                serviceMetas.add(meta);
            }
//            此步需要，如果serviceMetas为new出来的需要放入map中
            serviceList.put(serviceName,serviceMetas);
            log.info("当前服务列表："+serviceList);
        }
    }

    /**
     * 一个服务名对应的服务列表是集群，因此要考虑集群
     * 移除一台机器上所有的服务列表
     * @param serviceMetas
     */
    public void removeOneService(List<ServiceMeta> serviceMetas){
//        将该机器的所有服务进行遍历
        for (ServiceMeta serviceMeta : serviceMetas) {
//            获取服务名
            String serviceName = serviceMeta.getServiceName();
//            获取该服务名下缓存的服务集群元数据
            List<ServiceMeta> curServiceMetas = serviceList.get(serviceName);
//        如果该服务只有一台机器
            if(curServiceMetas.size() == 1){
//                直接剔除该服务
                serviceList.remove(serviceName);
            }else {
//          如果不止一台机器则去除一个ServiceMeta，但仍保留该服务
                curServiceMetas.remove(serviceMeta);
            }
        }
    }

    /**
     * 一个服务名对应的服务列表是集群，因此要考虑集群
     * 更新一台机器的所有服务列表更新时间
     * @param serviceMetas
     */
    public void updateServerTime(List<ServiceMeta> serviceMetas,long newTime){
//           将该机器的所有服务进行遍历
        for (ServiceMeta serviceMeta : serviceMetas) {
            String serviceName = serviceMeta.getServiceName();
//            获取该服务名下缓存的服务集群元数据
            List<ServiceMeta> curServiceMetas = serviceList.get(serviceName);
            int index = curServiceMetas.indexOf(serviceMeta);
//            更新时间并保存
            serviceMeta.setUpdateTime(newTime);
            curServiceMetas.set(index,serviceMeta);
        }
    }

    public void updateService(ConcurrentHashMap<String, List<ServiceMeta>> newServiceList){
        this.serviceList = newServiceList;
    }

    /**
     * 获取指定服务进行服务调用！
     * @param serviceName
     * @return
     */
    public List<ServiceMeta> getService(String serviceName){
        return serviceList.getOrDefault(serviceName, new ArrayList<>());
    }

}
