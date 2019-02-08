package cn.dingyuegroup.gray.server.service.impl;

import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayPolicyGroup;
import cn.dingyuegroup.gray.server.manager.GrayServiceManager;
import cn.dingyuegroup.gray.server.mysql.dao.GrayInstanceMapper;
import cn.dingyuegroup.gray.server.mysql.dao.GrayRbacResourcesMapper;
import cn.dingyuegroup.gray.server.mysql.dao.GrayServiceMapper;
import cn.dingyuegroup.gray.server.mysql.entity.GrayInstanceEntity;
import cn.dingyuegroup.gray.server.mysql.entity.GrayRbacResources;
import cn.dingyuegroup.gray.server.mysql.entity.GrayServiceEntity;
import cn.dingyuegroup.gray.server.service.AbstractGrayService;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 170147 on 2019/1/9.
 */
public class EurekaGrayService extends AbstractGrayService {

    private final EurekaClient eurekaClient;
    private final GrayServiceMapper grayServiceMapper;
    private final GrayInstanceMapper grayInstanceMapper;
    private final GrayServiceManager grayServiceManager;
    private final GrayRbacResourcesMapper grayRbacResourcesMapper;

    public EurekaGrayService(EurekaClient eurekaClient, DiscoveryClient discoveryClient,
                             GrayServiceMapper grayServiceMapper, GrayInstanceMapper grayInstanceMapper,
                             GrayServiceManager grayServiceManager, GrayRbacResourcesMapper grayRbacResourcesMapper) {
        super(discoveryClient, grayServiceMapper);
        this.eurekaClient = eurekaClient;
        this.grayServiceMapper = grayServiceMapper;
        this.grayInstanceMapper = grayInstanceMapper;
        this.grayServiceManager = grayServiceManager;
        this.grayRbacResourcesMapper = grayRbacResourcesMapper;
    }

    @Override
    public List<String> upInstanceIds(String serviceId) {
        //从eureka获取在线服务实例详细信息
        Application app = eurekaClient.getApplication(serviceId);
        if (app == null) {
            return new ArrayList<>();
        }
        List<InstanceInfo> instanceInfos = app.getInstances();
        if (CollectionUtils.isEmpty(instanceInfos)) {
            return new ArrayList<>();
        }
        List<String> list = instanceInfos.stream().map(InstanceInfo::getInstanceId).collect(Collectors.toList());
        return list;
    }

    @Override
    public List<GrayInstance> upInstances(String serviceId) {
        //从eureka获取在线服务实例详细信息
        Application app = eurekaClient.getApplication(serviceId);
        if (app == null) {
            return new ArrayList<>();
        }
        List<InstanceInfo> instanceInfos = app.getInstances();
        if (CollectionUtils.isEmpty(instanceInfos)) {
            return new ArrayList<>();
        }
        GrayServiceEntity grayServiceEntity = grayServiceMapper.selectByServiceId(serviceId);
        List<GrayInstance> list = new ArrayList<>();
        instanceInfos.stream().forEach(e -> {
            GrayInstance grayInstance = new GrayInstance();
            grayInstance.setServiceId(serviceId);
            grayInstance.setInstanceId(e.getInstanceId());
            grayInstance.setStatus(true);//在线
            grayInstance.setOpenGray(true);//默认是开启灰度状态
            grayInstance.setMetadata(e.getMetadata());
            grayInstance.setUrl(e.getHomePageUrl());
            if (grayServiceEntity != null) {
                grayInstance.setAppName(grayServiceEntity.getAppName());
            }
            GrayInstanceEntity grayInstanceEntity = grayInstanceMapper.selectByInstanceId(e.getInstanceId());
            if (grayInstanceEntity != null) {
                grayInstance.setOpenGray(grayInstanceEntity.getOpenGray() == 0 ? false : true);
                //eureka在线，并且持久化状态也是在线
                grayInstance.setStatus(grayInstance.isStatus() && (grayInstanceEntity.getStatus() == 0 ? false : true));
                grayInstance.setRemark(grayInstanceEntity.getRemark());
                if (!StringUtils.isEmpty(grayInstanceEntity.getEnv())) {
                    grayInstance.setEnv(grayInstanceEntity.getEnv());
                    GrayRbacResources resources = grayRbacResourcesMapper.selectByResource(grayInstanceEntity.getEnv());
                    if (resources != null) {
                        grayInstance.setEnvName(resources.getResourceName());
                    }
                }
            }
            //获取服务实例下的灰度策略组
            GrayPolicyGroup grayPolicyGroup = grayServiceManager.getGrayPolicyGroup(serviceId, e.getInstanceId());
            grayInstance.setGrayPolicyGroup(grayPolicyGroup);
            list.add(grayInstance);
        });
        return list;

    }
}
