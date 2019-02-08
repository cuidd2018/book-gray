package cn.dingyuegroup.gray.server.manager;

import cn.dingyuegroup.gray.core.*;
import cn.dingyuegroup.gray.server.config.properties.GrayServerConfig;
import cn.dingyuegroup.gray.server.context.GrayServerContext;
import cn.dingyuegroup.gray.server.model.vo.GrayPolicyGroupVO;
import cn.dingyuegroup.gray.server.model.vo.GrayPolicyVO;
import cn.dingyuegroup.gray.server.mysql.dao.*;
import cn.dingyuegroup.gray.server.mysql.entity.*;
import cn.dingyuegroup.gray.server.service.AbstractGrayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
     * 添加服务
     *
     * @param appName
     * @param serviceId
     * @param remark
     */
    @Override
    public void addService(String appName, String serviceId, String remark) {
        try {
            GrayServiceEntity entity = new GrayServiceEntity();
            entity.setAppName(appName);
            entity.setServiceId(serviceId);
            entity.setRemark(remark);
            entity.setCreateTime(new Date());
            entity.setIsDelete(0);
            grayServiceMapper.insert(entity);
        } catch (Exception e) {
            logger.error("addService:{}", e);
        }
    }

    /**
     * 编辑服务
     *
     * @param appName
     * @param serviceId
     * @param remark
     */
    @Override
    public void editService(String appName, String serviceId, String remark) {
        GrayServiceEntity entity = grayServiceMapper.selectByServiceId(serviceId);
        if (entity == null) {
            addService(appName, serviceId, remark);
        } else {
            entity.setAppName(appName);
            entity.setServiceId(serviceId);
            entity.setRemark(remark);
            entity.setUpdateTime(new Date());
            grayServiceMapper.updateByServiceId(entity);
        }
    }

    /**
     * 删除服务
     *
     * @param serviceId
     */
    @Override
    public void deleteService(String serviceId) {
        grayServiceMapper.deleteByServiceId(serviceId);
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
        grayService.setStatus(false);//不在线
        GrayServiceEntity entity = grayServiceMapper.selectByServiceId(serviceId);
        if (entity != null) {//持久化的状态是在线，并且从eureka获取的状态也是在线
            grayService.setAppName(entity.getAppName());
            grayService.setRemark(entity.getRemark());
        }
        List<GrayInstance> grayInstances = getInstances(serviceId);
        grayService.setGrayInstances(grayInstances);
        grayService.setStatus(grayService.isOnline());
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
        }
        GrayInstanceEntity grayInstanceEntity = grayInstanceMapper.selectByInstanceId(instanceId);
        if (grayInstanceEntity != null) {
            grayInstance.setOpenGray(grayInstanceEntity.getOpenGray() == 0 ? false : true);
            grayInstance.setStatus(grayInstance.isStatus() && (grayInstanceEntity.getStatus() == 0 ? false : true));
            grayInstance.setRemark(grayInstanceEntity.getRemark());
        }
        GrayPolicyGroup grayPolicyGroup = getGrayPolicyGroup(serviceId, instanceId);
        grayInstance.setGrayPolicyGroup(grayPolicyGroup);
        return grayInstance;
    }

    /**
     * 编辑服务实例
     *
     * @param serviceId
     * @param instanceId
     * @param remark
     */
    @Override
    public void editInstance(String serviceId, String instanceId, String remark) {
        GrayInstanceEntity entity = grayInstanceMapper.selectByInstanceId(instanceId);
        if (entity == null) {
            entity = new GrayInstanceEntity();
            entity.setServiceId(serviceId);
            entity.setInstanceId(instanceId);
            entity.setRemark(remark);
            entity.setCreateTime(new Date());
            entity.setOpenGray(1);
            entity.setStatus(0);
            grayInstanceMapper.insert(entity);
        } else {
            entity.setRemark(remark);
            entity.setUpdateTime(new Date());
            grayInstanceMapper.updateByInstanceId(entity);
        }
    }

    /**
     * 删除服务实例
     *
     * @param serviceId
     * @param instanceId
     */
    @Override
    public void deleteInstance(String serviceId, String instanceId) {
        grayInstanceMapper.deleteByInstanceId(instanceId);
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
                entity.setOpenGray(1);
                entity.setStatus(0);
                entity.setRemark("系统自动添加");
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
                entity.setStatus(status);
                entity.setCreateTime(new Date());
                entity.setOpenGray(1);
                entity.setStatus(0);
                entity.setRemark("系统自动添加");
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
     * 更新服务实例的资源环境
     *
     * @param serviceId
     * @param instanceId
     * @param env
     * @return
     */
    @Override
    public boolean updateInstanceEnv(String serviceId, String instanceId, String env) {
        if (StringUtils.isEmpty(serviceId) || StringUtils.isEmpty(instanceId) || StringUtils.isEmpty(env)) {
            return false;
        }
        GrayInstanceEntity entity = grayInstanceMapper.selectByInstanceId(instanceId);
        if (entity == null) {
            entity = new GrayInstanceEntity();
            entity.setServiceId(serviceId);
            entity.setInstanceId(instanceId);
            entity.setEnv(env);
            entity.setRemark("系统自动添加");
            entity.setCreateTime(new Date());
            entity.setOpenGray(1);
            entity.setStatus(0);
            GrayInstance grayInstance = getGrayInstance(serviceId, instanceId);
            if (grayInstance != null) {
                entity.setOpenGray(grayInstance.hasGrayPolicy() ? 1 : 0);
                entity.setStatus(grayInstance.isStatus() ? 1 : 0);
            }
            grayInstanceMapper.insert(entity);
        } else if (!env.equals(entity.getEnv())) {//当前资源环境更换
            entity.setEnv(env);
            grayInstanceMapper.updateEnvByInstanceId(entity);
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
        grayPolicyGroupMapper.updateByPolicyGroupId(entity);
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
    public boolean addPolicyGroup(String alias, Integer enable, String groupType, String remark) {
        GrayPolicyGroupEntity entity = new GrayPolicyGroupEntity();
        entity.setPolicyGroupId(GrayPolicyGroup.genId());
        entity.setEnable(enable);
        entity.setAlias(alias);
        entity.setGroupType(groupType == null ? GrayPolicyGroup.TYPE.AND.name() : groupType);
        entity.setRemark(remark);
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
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean delPolicyGroup(String groupId) {
        grayPolicyGroupMapper.deleteByGroupId(groupId);
        grayPolicyGroupPolicyMapper.deleteByGroupId(groupId);
        return true;
    }

    /**
     * 编辑策略组
     *
     * @param groupId
     * @param alias
     * @return
     */
    @Override
    public boolean editPolicyGroup(String groupId, String alias, String groupType, String remark) {
        GrayPolicyGroupEntity entity = new GrayPolicyGroupEntity();
        entity.setPolicyGroupId(groupId);
        entity.setAlias(alias);
        entity.setGroupType(groupType);
        entity.setRemark(remark);
        grayPolicyGroupMapper.editByPolicyGroupId(entity);
        return true;
    }

    /**
     * 添加策略
     *
     * @param policyType
     * @return
     */
    @Override
    public boolean addPolicy(String policyType, String policyKey, String policyValue, String policyMatchType, String policyName, String remark) {
        GrayPolicyEntity grayPolicyEntity = new GrayPolicyEntity();
        grayPolicyEntity.setPolicyId(GrayPolicy.genId());
        grayPolicyEntity.setPolicyKey(policyKey);
        grayPolicyEntity.setPolicyValue(policyValue);
        grayPolicyEntity.setPolicyMatchType(policyMatchType);
        grayPolicyEntity.setCreateTime(new Date());
        grayPolicyEntity.setPolicyType(policyType);
        grayPolicyEntity.setPolicyName(policyName);
        grayPolicyEntity.setRemark(remark);
        grayPolicyMapper.insert(grayPolicyEntity);
        return true;
    }

    /**
     * 编辑策略
     *
     * @param policyId
     * @param policyType
     * @return
     */
    @Override
    public boolean editPolicy(String policyId, String policyType, String policyKey, String policyValue, String policyMatchType, String policyName, String remark) {
        GrayPolicyEntity grayPolicyEntity = new GrayPolicyEntity();
        grayPolicyEntity.setPolicyId(policyId);
        grayPolicyEntity.setPolicyKey(policyKey);
        grayPolicyEntity.setPolicyValue(policyValue);
        grayPolicyEntity.setPolicyMatchType(policyMatchType);
        grayPolicyEntity.setUpdateTime(new Date());
        grayPolicyEntity.setPolicyType(policyType);
        grayPolicyEntity.setRemark(remark);
        grayPolicyEntity.setPolicyName(policyName);
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
        GrayPolicyGroupEntity groupEntity = grayPolicyGroupMapper.selectByPolicyGroupId(groupId);
        if (groupEntity == null) {
            return false;
        }
        GrayPolicyEntity policyEntity = grayPolicyMapper.selectByPolicyId(policyId);
        if (policyEntity == null) {
            return false;
        }
        lock.lock();
        try {
            GrayPolicyGroupPolicy grayPolicyGroupPolicy = new GrayPolicyGroupPolicy();
            grayPolicyGroupPolicy.setPolicyGroupId(groupId);
            grayPolicyGroupPolicy.setPolicyId(policyId);
            int count = grayPolicyGroupPolicyMapper.countByGroupIdAndPolicyId(grayPolicyGroupPolicy);
            if (count == 0) {
                grayPolicyGroupPolicyMapper.insert(grayPolicyGroupPolicy);
            }
        } finally {
            lock.unlock();
        }
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
        GrayPolicyGroupEntity groupEntity = grayPolicyGroupMapper.selectByPolicyGroupId(groupId);
        if (groupEntity == null) {
            return false;
        }
        GrayPolicyEntity policyEntity = grayPolicyMapper.selectByPolicyId(policyId);
        if (policyEntity == null) {
            return false;
        }
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
    public boolean editInstancePolicyGroup(String serviceId, String instanceId, String groupId) {
        lock.lock();
        try {
            GrayPolicyGroupEntity grayPolicyGroupEntity = grayPolicyGroupMapper.selectByPolicyGroupId(groupId);
            if (grayPolicyGroupEntity == null) {
                return false;
            }
            GrayInstanceEntity grayInstanceEntity = grayInstanceMapper.selectByInstanceId(instanceId);
            if (grayInstanceEntity == null) {
                grayInstanceEntity = new GrayInstanceEntity();
                grayInstanceEntity.setServiceId(serviceId);
                grayInstanceEntity.setInstanceId(instanceId);
                grayInstanceEntity.setPolicyGroupId(groupId);
                grayInstanceEntity.setCreateTime(new Date());
                grayInstanceEntity.setOpenGray(1);
                grayInstanceEntity.setStatus(0);
                grayInstanceMapper.insert(grayInstanceEntity);
            } else {
                grayInstanceEntity.setPolicyGroupId(groupId);
                grayInstanceEntity.setUpdateTime(new Date());
                grayInstanceEntity.setInstanceId(instanceId);
                grayInstanceMapper.updatePolicyGroupByInstanceId(grayInstanceEntity);
            }
            return true;
        } finally {
            lock.unlock();
        }
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
        GrayInstanceEntity grayInstanceEntity = new GrayInstanceEntity();
        grayInstanceEntity.setInstanceId(instanceId);
        grayInstanceEntity.setUpdateTime(new Date());
        grayInstanceEntity.setPolicyGroupId(null);
        grayInstanceMapper.updatePolicyGroupByInstanceId(grayInstanceEntity);
        return true;
    }

    /**
     * 获取策略组下的灰度策略
     *
     * @param groupId
     * @return
     */
    @Override
    public List<GrayPolicyVO> listGrayPolicyByGroup(String groupId) {
        GrayPolicyGroupEntity grayPolicyGroupEntity = grayPolicyGroupMapper.selectByPolicyGroupId(groupId);
        if (grayPolicyGroupEntity == null) {
            return new ArrayList<>();
        }
        //获取策略组和策略的对应关系
        List<GrayPolicyGroupPolicy> grayPolicyGroupPolicies = grayPolicyGroupPolicyMapper.selectByPolicyGroupId(groupId);
        if (grayPolicyGroupPolicies == null) {
            grayPolicyGroupPolicies = new ArrayList<>();
        }
        List<GrayPolicyVO> list = new ArrayList<>();
        grayPolicyGroupPolicies.stream().forEach(f -> {
            //获取每个策略的具体信息
            GrayPolicyEntity grayPolicyEntity = grayPolicyMapper.selectByPolicyId(f.getPolicyId());
            if (grayPolicyEntity != null) {
                GrayPolicyVO grayPolicyVO = new GrayPolicyVO();
                grayPolicyVO.setPolicyGroupId(groupId);
                grayPolicyVO.setGroupType(grayPolicyGroupEntity.getGroupType());
                grayPolicyVO.setAlias(grayPolicyGroupEntity.getAlias());
                grayPolicyVO.setPolicyId(grayPolicyEntity.getPolicyId());
                grayPolicyVO.setRemark(grayPolicyEntity.getRemark());
                grayPolicyVO.setPolicyKey(grayPolicyEntity.getPolicyKey());
                grayPolicyVO.setPolicyMatchType(grayPolicyEntity.getPolicyMatchType());
                grayPolicyVO.setPolicyType(grayPolicyEntity.getPolicyType());
                grayPolicyVO.setPolicyValue(grayPolicyEntity.getPolicyValue());
                list.add(grayPolicyVO);
            }
        });
        return list;
    }

    /**
     * 获取所有灰度策略
     *
     * @return
     */
    @Override
    public List<GrayPolicyVO> listAllGrayPolicy() {
        List<GrayPolicyEntity> list = grayPolicyMapper.selectAll();
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<GrayPolicyVO> grayPolicyVOS = new ArrayList<>();
        list.forEach(e -> {
            GrayPolicyVO grayPolicyVO = new GrayPolicyVO();
            grayPolicyVO.setPolicyId(e.getPolicyId());
            grayPolicyVO.setRemark(e.getRemark());
            grayPolicyVO.setPolicyKey(e.getPolicyKey());
            grayPolicyVO.setPolicyMatchType(e.getPolicyMatchType());
            grayPolicyVO.setPolicyType(e.getPolicyType());
            grayPolicyVO.setPolicyValue(e.getPolicyValue());
            grayPolicyVO.setPolicyName(e.getPolicyName());
            grayPolicyVOS.add(grayPolicyVO);
        });
        return grayPolicyVOS;
    }

    /**
     * 获取所有策略组列表
     *
     * @return
     */
    @Override
    public List<GrayPolicyGroupVO> listAllGrayPolicyGroup() {
        List<GrayPolicyGroupEntity> list = grayPolicyGroupMapper.selectAll();
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }
        List<GrayPolicyGroupVO> grayPolicyGroups = new ArrayList<>();
        list.forEach(e -> {
            GrayPolicyGroupVO group = new GrayPolicyGroupVO();
            group.setAlias(e.getAlias());
            group.setPolicyGroupId(e.getPolicyGroupId());
            group.setEnable(e.getEnable() == 0 ? false : true);
            group.setGroupType(e.getGroupType());
            group.setRemark(e.getRemark());
            grayPolicyGroups.add(group);
        });
        return grayPolicyGroups;
    }

    /**
     * 获取服务实例下的灰度策略组
     *
     * @param serviceId
     * @param instanceId
     * @return
     */
    @Override
    public GrayPolicyGroup getGrayPolicyGroup(String serviceId, String instanceId) {
        //获取服务实例下的灰度策略组集合
        GrayInstanceEntity grayInstanceEntity = grayInstanceMapper.selectByInstanceId(instanceId);
        if (grayInstanceEntity == null || StringUtils.isEmpty(grayInstanceEntity.getPolicyGroupId())) {
            return null;
        }
        GrayPolicyGroupEntity grayPolicyGroupEntity = grayPolicyGroupMapper.selectByPolicyGroupId(grayInstanceEntity.getPolicyGroupId());
        if (grayPolicyGroupEntity == null) {
            return null;
        }
        List<GrayPolicyVO> grayPolicyEntities = listGrayPolicyByGroup(grayInstanceEntity.getPolicyGroupId());
        //封装
        GrayPolicyGroup grayPolicyGroup = new GrayPolicyGroup();
        grayPolicyGroup.setPolicyGroupId(grayPolicyGroupEntity.getPolicyGroupId());
        grayPolicyGroup.setAlias(grayPolicyGroupEntity.getAlias());
        grayPolicyGroup.setEnable(grayPolicyGroupEntity.getEnable() == 0 ? false : true);
        grayPolicyGroup.setGroupType(grayPolicyGroupEntity.getGroupType());//策略类型，与和或
        //获取策略组下的策略集合
        grayPolicyEntities.stream().forEach(m -> {
            GrayPolicy grayPolicy = new GrayPolicy();
            grayPolicy.setPolicyId(m.getPolicyId());
            grayPolicy.setPolicyType(m.getPolicyType());
            Map<String, String> params = new HashMap<>();
            params.put(GrayPolicy.POLICY.POLICY_KEY.name(), m.getPolicyKey());
            params.put(GrayPolicy.POLICY.POLICY_VALUE.name(), m.getPolicyValue());
            params.put(GrayPolicy.POLICY.POLICY_MATCH_TYPE.name(), m.getPolicyMatchType());
            grayPolicy.setInfos(params);
            grayPolicyGroup.addGrayPolicy(grayPolicy);
        });
        return grayPolicyGroup;
    }

    /**
     * 打开检查
     */
    @Override
    public void openForWork() {
        try {//更新本地资源环境
            InstanceLocalInfo localInfo = GrayServerContext.getInstanceLocalInfo();
            Environment environment = GrayServerContext.getEnvironment();
            if (localInfo != null && environment != null) {
                updateInstanceEnv(localInfo.getServiceId(), localInfo.getInstanceId(), environment.getActiveProfiles()[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
