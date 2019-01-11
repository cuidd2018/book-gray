package cn.dingyuegroup.gray.server.manager;

import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayPolicy;
import cn.dingyuegroup.gray.core.GrayPolicyGroup;
import cn.dingyuegroup.gray.core.GrayService;
import cn.dingyuegroup.gray.server.config.properties.GrayServerConfig;
import cn.dingyuegroup.gray.server.context.GrayServerContext;
import cn.dingyuegroup.gray.server.mysql.dao.*;
import cn.dingyuegroup.gray.server.mysql.entity.*;
import cn.dingyuegroup.gray.server.service.AbstractGrayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class DefaultGrayServiceManager implements GrayServiceManager {

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
    private GrayServerConfig serverConfig;
    @Autowired
    private AbstractGrayService abstractGrayService;

    private Lock lock = new ReentrantLock();

    private Timer evictionTimer = new Timer("Gray-EvictionTimer", true);

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
        if (entityList == null) {
            entityList = new ArrayList<>();
        }
        //从eureka获取在线服务
        List<String> upServiceIds = abstractGrayService.upServiceIds();
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
        List<GrayService> upGrayServices = abstractGrayService.upServices();
        if (!CollectionUtils.isEmpty(upGrayServices)) {
            Optional<GrayService> optional = upGrayServices.parallelStream().filter(e -> e.getServiceId().equals(serviceId)).findAny();
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        GrayService grayService = new GrayService();
        grayService.setServiceId(serviceId);
        grayService.setStatus(false);
        GrayServiceEntity entity = grayServiceMapper.selectByServiceId(serviceId);
        if (entity != null) {//持久化的状态是在线，并且从eureka获取的状态也是在线
            grayService.setAppName(entity.getAppName());
            grayService.setStatus(grayService.isStatus() && entity.getStatus() == 0 ? false : true);
        }
        List<GrayInstance> grayInstances = getInstances(serviceId);
        grayService.setGrayInstances(grayInstances);
        return grayService;
    }

    /**
     * 更新服务的在线状态
     *
     * @param serviceId
     * @param status
     * @return
     */
    @Override
    public boolean editGrayServiceOnlineStatus(String serviceId, int status) {
        lock.lock();
        try {
            GrayServiceEntity entity = grayServiceMapper.selectByServiceId(serviceId);
            if (entity == null) {
                entity = new GrayServiceEntity();
                entity.setServiceId(serviceId);
                entity.setCreateTime(new Date());
                entity.setIsDelete(0);
                entity.setStatus(status);
                grayServiceMapper.insert(entity);
            } else {
                entity.setStatus(status);
                entity.setUpdateTime(new Date());
                grayServiceMapper.updateStatusByServiceId(entity);
            }
            logger.info("更新服务实例在线状态成功：serviceId:{}", serviceId);
        } finally {
            lock.unlock();
        }
        return true;
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
        if (grayInstanceEntityList == null) {
            grayInstanceEntityList = new ArrayList<>();
        }
        //从eureka获取在线服务实例详细信息
        List<String> upIds = abstractGrayService.upInstanceIds(serviceId);
        List<String> entityIds = grayInstanceEntityList.stream().map(GrayInstanceEntity::getInstanceId).collect(Collectors.toList());
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
        List<GrayInstance> instanceInfos = abstractGrayService.upInstances(serviceId);
        if (!CollectionUtils.isEmpty(instanceInfos)) {
            Optional<GrayInstance> optional = instanceInfos.stream().filter(e -> e.getInstanceId().equals(instanceId)).findAny();
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        GrayInstance grayInstance = new GrayInstance();
        grayInstance.setServiceId(serviceId);
        grayInstance.setInstanceId(instanceId);
        grayInstance.setStatus(false);//不在线
        grayInstance.setOpenGray(true);//默认是开启灰度状态
        GrayServiceEntity grayServiceEntity = grayServiceMapper.selectByServiceId(serviceId);
        if (grayServiceEntity != null) {
            grayInstance.setAppName(grayServiceEntity.getAppName());
            grayInstance.setStatus(grayInstance.isStatus() && grayServiceEntity.getStatus() == 0 ? false : true);
        }
        GrayInstanceEntity grayInstanceEntity = grayInstanceMapper.selectByInstanceId(instanceId);
        if (grayInstanceEntity != null) {
            grayInstance.setOpenGray(grayInstanceEntity.getOpenGray() == 0 ? false : true);
            grayInstance.setStatus(grayInstance.isStatus() && grayInstanceEntity.getStatus() == 0 ? false : true);
        }
        List<GrayPolicyGroup> grayPolicyGroups = getGrayPolicyGroup(serviceId, instanceId);
        grayInstance.setGrayPolicyGroups(grayPolicyGroups);
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
    public boolean editInstanceGrayStatus(String serviceId, String instanceId, int status) {
        lock.lock();
        try {
            GrayInstanceEntity entity = grayInstanceMapper.selectByInstanceId(instanceId);
            if (entity == null) {
                entity = new GrayInstanceEntity();
                entity.setInstanceId(instanceId);
                entity.setServiceId(serviceId);
                entity.setOpenGray(status);
                entity.setCreateTime(new Date());
                grayInstanceMapper.insert(entity);
            } else {
                entity.setUpdateTime(new Date());
                entity.setOpenGray(status);
                grayInstanceMapper.updateGrayStatusByInstanceId(entity);
            }
            logger.info("更新服务实例灰度状态成功：serviceId:{}，instanceId:{}", serviceId, instanceId);
        } finally {
            lock.unlock();
        }
        return true;
    }

    /**
     * 更新服务实例的在线状态
     *
     * @param serviceId
     * @param instanceId
     * @param status
     * @return
     */
    @Override
    public boolean editInstanceOnlineStatus(String serviceId, String instanceId, int status) {
        lock.lock();
        try {
            GrayInstanceEntity entity = grayInstanceMapper.selectByInstanceId(instanceId);
            if (entity == null) {
                entity = new GrayInstanceEntity();
                entity.setInstanceId(instanceId);
                entity.setServiceId(serviceId);
                entity.setOpenGray(1);
                entity.setStatus(status);
                entity.setCreateTime(new Date());
                grayInstanceMapper.insert(entity);
            } else {
                entity.setStatus(status);
                entity.setUpdateTime(new Date());
                grayInstanceMapper.updateStatusByInstanceId(entity);
            }
            logger.info("更新服务实例灰度状态成功：serviceId:{}，instanceId:{}", serviceId, instanceId);
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

    /**
     * 获取服务实例下的灰度策略组
     *
     * @param serviceId
     * @param instanceId
     * @return
     */
    @Override
    public List<GrayPolicyGroup> getGrayPolicyGroup(String serviceId, String instanceId) {
        List<GrayPolicyGroup> list = new ArrayList<>();
        //获取服务实例下的灰度策略组集合
        List<GrayPolicyGroupEntity> grayPolicyGroupEntities = grayInstancePolicyGroupMapper.selectPolicyGroupByInstanceId(instanceId);
        if (grayPolicyGroupEntities == null) {
            grayPolicyGroupEntities = new ArrayList<>();
        }
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
            list.add(grayPolicyGroup);
        });
        return list;
    }

    /**
     * 打开检查
     */
    @Override
    public void openForWork() {
        evictionTimer.schedule(new EvictionTask(),
                serverConfig.getEvictionIntervalTimerInMs(),
                serverConfig.getEvictionIntervalTimerInMs());
    }

    @Override
    public void shutdown() {

    }

    protected void evict() {
        GrayServerContext.getGrayServerEvictor().evict(this);
    }

    class EvictionTask extends TimerTask {

        @Override
        public void run() {
            evict();
        }
    }

}
