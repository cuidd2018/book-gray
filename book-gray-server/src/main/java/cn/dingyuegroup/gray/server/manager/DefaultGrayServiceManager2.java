package cn.dingyuegroup.gray.server.manager;

import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayPolicy;
import cn.dingyuegroup.gray.core.GrayPolicyGroup;
import cn.dingyuegroup.gray.core.GrayService;
import cn.dingyuegroup.gray.server.mysql.dao.*;
import cn.dingyuegroup.gray.server.mysql.entity.*;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Created by 170147 on 2019/1/8.
 */
@Service
public class DefaultGrayServiceManager2 implements GrayServiceManager2 {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GrayServiceMapper grayServiceMapper;
    @Autowired
    private GrayInstanceMapper grayInstanceMapper;
    @Autowired
    @Qualifier(value = "GrayInstancePolicyGroupMapperProxy")
    private GrayInstancePolicyGroupMapper grayInstancePolicyGroupMapper;
    @Autowired
    private GrayPolicyGroupMapper grayPolicyGroupMapper;
    @Autowired
    private GrayPolicyMapper grayPolicyMapper;
    @Autowired
    private GrayPolicyGroupPolicyMapper grayPolicyGroupPolicyMapper;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private EurekaClient eurekaClient;

    private Lock lock = new ReentrantLock();

    /**
     * 获取全部的灰度服务实例
     *
     * @return
     */
    @Override
    public List<GrayService> getServices() {
        List<GrayService> services = new ArrayList<>();
        //获取持久化的服务信息
        List<GrayServiceEntity> entityList = grayServiceMapper.selectAll();
        //从eureka获取在线服务
        List<String> upServiceIds = discoveryClient.getServices();
        List<String> entityIds = entityList.stream().map(GrayServiceEntity::getServiceId).collect(Collectors.toList());
        entityIds.removeAll(upServiceIds);//去重
        upServiceIds.addAll(entityIds);//取并集
        upServiceIds.stream().forEach(e -> {
            GrayService grayService = getGrayService(e);
            services.add(grayService);
        });
        return services;
    }

    /**
     * 获取服务信息
     *
     * @param serviceId
     * @return
     */
    @Override
    public GrayService getGrayService(String serviceId) {
        GrayService grayService = new GrayService();
        grayService.setServiceId(serviceId);
        grayService.setStatus(false);
        GrayServiceEntity entity = grayServiceMapper.selectByServiceId(serviceId);
        if (entity != null) {
            grayService.setAppName(entity.getAppName());
        }
        //从eureka获取在线服务
        List<String> upServiceIds = discoveryClient.getServices();
        if (!CollectionUtils.isEmpty(upServiceIds)) {
            Optional<String> optional = upServiceIds.parallelStream().filter(e -> e.equals(serviceId)).findAny();
            if (optional.isPresent()) {
                grayService.setStatus(true);//在线状态
            }
        }
        List<GrayInstance> grayInstances = getInstances(serviceId);
        grayService.setGrayInstances(grayInstances);
        return grayService;
    }

    /**
     * 获取服务下的所有实例
     *
     * @param serviceId
     * @return
     */
    @Override
    public List<GrayInstance> getInstances(String serviceId) {
        List<GrayInstance> list = new ArrayList<>();
        //获取持久化的服务实例信息
        List<GrayInstanceEntity> grayInstanceEntityList = grayInstanceMapper.selectByServiceId(serviceId);
        //从eureka获取在线服务实例详细信息
        Application app = eurekaClient.getApplication(serviceId);
        List<InstanceInfo> instanceInfos = app.getInstances();
        List<String> entityIds = grayInstanceEntityList.stream().map(GrayInstanceEntity::getInstanceId).collect(Collectors.toList());
        List<String> upIds = instanceInfos.stream().map(InstanceInfo::getInstanceId).collect(Collectors.toList());
        entityIds.removeAll(upIds);//去重
        upIds.addAll(entityIds);//取并集
        upIds.stream().forEach(e -> {
            GrayInstance grayInstance = getGrayInstance(serviceId, e);
            list.add(grayInstance);
        });
        return list;
    }

    /**
     * 获取某个服务实例下的所有灰度策略组
     *
     * @param instanceId
     * @return
     */
    @Override
    public GrayInstance getGrayInstance(String serviceId, String instanceId) {
        GrayInstance grayInstance = new GrayInstance();
        grayInstance.setServiceId(serviceId);
        grayInstance.setInstanceId(instanceId);
        grayInstance.setStatus(false);//不在线
        grayInstance.setOpenGray(true);//默认是开启灰度状态
        GrayInstanceEntity grayInstanceEntity = grayInstanceMapper.selectByInstanceId(instanceId);
        if (grayInstanceEntity != null) {
            grayInstance.setOpenGray(grayInstanceEntity.getOpenGray() == 0 ? false : true);
        }
        GrayServiceEntity grayServiceEntity = grayServiceMapper.selectByServiceId(serviceId);
        if (grayServiceEntity != null) {
            grayInstance.setAppName(grayServiceEntity.getAppName());
        }
        //从eureka获取在线服务实例详细信息
        Application app = eurekaClient.getApplication(serviceId);
        List<InstanceInfo> instanceInfos = app.getInstances();
        if (!CollectionUtils.isEmpty(instanceInfos)) {
            Optional<InstanceInfo> optional = instanceInfos.stream().filter(e -> e.getInstanceId().equals(instanceId)).findAny();
            if (optional.isPresent()) {
                grayInstance.setStatus(true);//在线
                grayInstance.setMetadata(optional.get().getMetadata());
                grayInstance.setUrl(optional.get().getHomePageUrl());
            }
        }
        //获取服务实例下的灰度策略组集合
        List<GrayPolicyGroupEntity> grayPolicyGroupEntities = grayInstancePolicyGroupMapper.selectPolicyGroupByInstanceId(instanceId);
        grayPolicyGroupEntities.stream().forEach(f -> {
            GrayPolicyGroup grayPolicyGroup = new GrayPolicyGroup();
            grayPolicyGroup.setPolicyGroupId(f.getPolicyGroupId());
            grayPolicyGroup.setAlias(f.getAlias());
            grayPolicyGroup.setEnable(f.getEnable() == 0 ? false : true);
            //获取策略组下的策略集合
            f.getGrayPolicyEntities().stream().forEach(m -> {
                GrayPolicy grayPolicy = new GrayPolicy();
                grayPolicy.setPolicyId(m.getPolicyId());
                grayPolicy.setPolicyType(m.getPolicyType());
                //TODO
                grayPolicy.setInfos(new HashMap<>());
                grayPolicyGroup.addGrayPolicy(grayPolicy);
            });
            grayInstance.getGrayPolicyGroups().add(grayPolicyGroup);
        });
        return grayInstance;
    }

    /**
     * 更新服务实例的灰度状态
     *
     * @param serviceId
     * @param instanceId
     * @param status
     * @return
     */
    @Override
    public boolean editInstanceStatus(String serviceId, String instanceId, int status) {
        lock.lock();
        try {
            GrayInstanceEntity entity = grayInstanceMapper.selectByInstanceId(instanceId);
            if (entity == null) {
                entity = new GrayInstanceEntity();
                entity.setInstanceId(instanceId);
                entity.setServiceId(serviceId);
                entity.setOpenGray(status);
                grayInstanceMapper.insert(entity);
            } else {
                grayInstanceMapper.updateByInstanceId(entity);
            }
            logger.info("更新服务实例灰度状态serviceId:{},instanceId:{}", serviceId, instanceId);
        } finally {
            lock.unlock();
        }
        return true;
    }

    /**
     * 更新策略组的状态
     *
     * @param serviceId
     * @param instanceId
     * @param groupId
     * @param enable
     * @return
     */
    @Override
    public boolean editPolicyGroupStatus(String serviceId, String instanceId, String groupId, int enable) {
        GrayPolicyGroupEntity entity = grayPolicyGroupMapper.selectByPolicyGroupId(groupId);
        if (entity == null) {
            return false;
        }
        entity.setEnable(enable);
        grayPolicyGroupMapper.updateByByPolicyGroupId(entity);
        return true;
    }

    /**
     * 添加策略组
     *
     * @param alias
     * @param enable
     * @return
     */
    @Override
    public boolean addPolicyGroup(String alias, Integer enable) {
        GrayPolicyGroupEntity entity = new GrayPolicyGroupEntity();
        entity.setPolicyGroupId(GrayPolicyGroup.genId());
        entity.setIsDelete(0);
        entity.setEnable(enable);
        entity.setAlias(alias);
        grayPolicyGroupMapper.insert(entity);
        return true;
    }

    /**
     * 删除策略组
     *
     * @param groupId
     * @return
     */
    @Override
    public boolean delPolicyGroup(String groupId) {
        grayPolicyGroupMapper.deleteByGroupId(groupId);
        return true;
    }

    /**
     * 编辑策略组
     *
     * @param groupId
     * @param alias
     * @param enable
     * @return
     */
    @Override
    public boolean editPolicyGroup(String groupId, String alias, Integer enable) {
        GrayPolicyGroupEntity entity = new GrayPolicyGroupEntity();
        entity.setPolicyGroupId(groupId);
        entity.setEnable(enable);
        entity.setAlias(alias);
        grayPolicyGroupMapper.editByByPolicyGroupId(entity);
        return false;
    }

    /**
     * 添加策略
     *
     * @param policyType
     * @param policy
     * @return
     */
    @Override
    public boolean addPolicy(String policyType, String policy) {
        GrayPolicyEntity grayPolicyEntity = new GrayPolicyEntity();
        grayPolicyEntity.setPolicyId(GrayPolicy.genId());
        grayPolicyEntity.setPolicy(policy);
        grayPolicyEntity.setCreateTime(new Date());
        grayPolicyEntity.setIsDelete(0);
        grayPolicyEntity.setPolicyType(policyType);
        grayPolicyMapper.insert(grayPolicyEntity);
        return true;
    }

    /**
     * 编辑策略
     *
     * @param policyId
     * @param policyType
     * @param policy
     * @return
     */
    @Override
    public boolean editPolicy(String policyId, String policyType, String policy) {
        GrayPolicyEntity grayPolicyEntity = new GrayPolicyEntity();
        grayPolicyEntity.setPolicyId(policyId);
        grayPolicyEntity.setPolicy(policy);
        grayPolicyEntity.setUpdateTime(new Date());
        grayPolicyEntity.setPolicyType(policyType);
        grayPolicyMapper.updateByPolicyId(grayPolicyEntity);
        return true;
    }

    /**
     * 删除策略组
     *
     * @param policyId
     * @return
     */
    @Override
    public boolean delPolicy(String policyId) {
        grayPolicyMapper.deleteByPolicyId(policyId);
        return true;
    }

    /**
     * 策略组添加策略
     *
     * @param groupId
     * @param policyId
     * @return
     */
    @Override
    public boolean addPolicyGroupPolicy(String groupId, String policyId) {
        GrayPolicyGroupPolicy grayPolicyGroupPolicy = new GrayPolicyGroupPolicy();
        grayPolicyGroupPolicy.setPolicyGroupId(groupId);
        grayPolicyGroupPolicy.setPolicyId(policyId);
        grayPolicyGroupPolicyMapper.insert(grayPolicyGroupPolicy);
        return true;
    }

    /**
     * 策略组删除策略
     *
     * @param groupId
     * @param policyId
     * @return
     */
    @Override
    public boolean delPolicyGroupPolicy(String groupId, String policyId) {
        GrayPolicyGroupPolicy grayPolicyGroupPolicy = new GrayPolicyGroupPolicy();
        grayPolicyGroupPolicy.setPolicyGroupId(groupId);
        grayPolicyGroupPolicy.setPolicyId(policyId);
        grayPolicyGroupPolicyMapper.deleteByGroupIdAndPolicyId(grayPolicyGroupPolicy);
        return true;
    }

    /**
     * 添加策略组
     *
     * @param serviceId
     * @return
     */
    @Override
    public boolean addInstancePolicyGroup(String serviceId, String instanceId, String groupId) {
        GrayPolicyGroupEntity grayPolicyGroupEntity = grayPolicyGroupMapper.selectByPolicyGroupId(groupId);
        if (grayPolicyGroupEntity == null) {
            return false;
        }
        GrayInstancePolicyGroup grayInstancePolicyGroup = new GrayInstancePolicyGroup();
        grayInstancePolicyGroup.setPolicyGroupId(groupId);
        grayInstancePolicyGroup.setInstanceId(instanceId);
        GrayInstancePolicyGroup entity = grayInstancePolicyGroupMapper.selectByInstanceIdAndGroupId(grayInstancePolicyGroup);
        if (entity == null) {
            grayInstancePolicyGroupMapper.insert(grayInstancePolicyGroup);
        }
        return true;
    }

    /**
     * 服务实例删除灰度策略组
     *
     * @param serviceId
     * @param instanceId
     * @param groupId
     * @return
     */
    @Override
    public boolean delInstancePolicyGroup(String serviceId, String instanceId, String groupId) {
        GrayInstancePolicyGroup grayInstancePolicyGroup = new GrayInstancePolicyGroup();
        grayInstancePolicyGroup.setPolicyGroupId(groupId);
        grayInstancePolicyGroup.setInstanceId(instanceId);
        grayInstancePolicyGroupMapper.deleteByInstanceIdAndGroupId(grayInstancePolicyGroup);
        return true;
    }

}
