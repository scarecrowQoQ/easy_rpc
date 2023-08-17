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
//  当前缓存的服务列表， key=serviceName，value为当前服务下的所有机器元数据
    private  ConcurrentHashMap<String, List<ServiceMeta>> serviceList = new ConcurrentHashMap<>();

    public void addService(List<ServiceMeta> metas){
        for (ServiceMeta meta : metas) {
            String serviceName = meta.getServiceName();
            List<ServiceMeta> serviceMetas = serviceList.getOrDefault(serviceName, new ArrayList<>());
            if(!serviceMetas.contains(meta)){
                serviceMetas.add(meta);
            }
            serviceList.put(serviceName,serviceMetas);
        }
    }

    public void removeOneService(List<ServiceMeta> serviceMetas){
//        将该机器的所有服务进行遍历
        for (ServiceMeta serviceMeta : serviceMetas) {
//            获取服务名
            String serviceName = serviceMeta.getServiceName();
//            获取该服务名下缓存的服务元数据
            List<ServiceMeta> curServiceMetas = serviceList.get(serviceName);
//        如果该服务只有一台机器
            if(curServiceMetas.size() == 1){
//                直接剔除该服务
                serviceList.remove(serviceMeta.getServiceName());
            }else {
//          如果不止一台机器则去除一个ServiceMeta，但仍保留该服务
                curServiceMetas.remove(serviceMeta);
                serviceList.put(serviceName,serviceMetas);
            }
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
