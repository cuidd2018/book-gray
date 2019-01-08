package cn.dingyuegroup.gray.server.manager;

import cn.dingyuegroup.gray.core.*;
import cn.dingyuegroup.gray.server.config.properties.GrayServerConfig;
import cn.dingyuegroup.gray.server.context.GrayServerContext;
import cn.dingyuegroup.gray.server.model.vo.GrayInstanceVO;
import cn.dingyuegroup.gray.server.model.vo.GrayServiceVO;
import cn.dingyuegroup.gray.server.mysql.dao.GrayInstanceMapper;
import cn.dingyuegroup.gray.server.mysql.dao.GrayInstancePolicyGroupMapper;
import cn.dingyuegroup.gray.server.mysql.entity.GrayInstanceEntity;
import cn.dingyuegroup.gray.server.mysql.entity.GrayPolicyGroupEntity;
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


/**
 * 维护一个Map, 用来管理GrayService，key是service id。
 * 并且每隔一段时间就调用EurekaGrayServerEvictor，检查列表中的实例是否下线，将下线的服务从灰度列表中删除。
 */
@Service
public class DefaultGrayServiceManager implements GrayServiceManager {

    Logger logger = LoggerFactory.getLogger(getClass());

    private Lock lock = new ReentrantLock();
    private Timer evictionTimer = new Timer("Gray-EvictionTimer", true);

    @Autowired
    private AbstractGrayService grayService;
    @Autowired
    private GrayServerConfig serverConfig;
    @Autowired
    private GrayInstanceMapper grayInstanceMapper;
    @Autowired
    @Qualifier(value = "GrayInstancePolicyGroupMapperProxy")
    private GrayInstancePolicyGroupMapper grayInstancePolicyGroupMapper;

    @Override
    public void addGrayInstance(GrayInstance instance) {
        lock.lock();
        GrayInstanceEntity entity = grayInstanceMapper.selectByInstanceId(instance.getInstanceId());
        try {
            if (entity == null) {
                entity = new GrayInstanceEntity();
                entity.setInstanceId(instance.getInstanceId());
                entity.setServiceId(instance.getServiceId());
                entity.setOpenGray(instance.isOpenGray() ? 1 : 0);
                grayInstanceMapper.insert(entity);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteGrayInstance(String serviceId, String instanceId) {
        lock.lock();
        try {
            int r = grayInstanceMapper.deleteByInstanceId(instanceId);
            logger.info("删除服务实例serviceId:{},instanceId:{},结果:{}", serviceId, instanceId, r);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void addGrayPolicy(String serviceId, String instanceId, String policyGroupId, GrayPolicy policy) {
        GrayInstance grayInstance = getGrayInstane(serviceId, instanceId);
        if (grayInstance != null) {
            grayInstance.addGrayPolicy(policyGroupId, policy);
        }
    }

    @Override
    public void deleteGrayPolicy(String serviceId, String instanceId, String policyGroupId, String policyId) {
        GrayInstance grayInstance = getGrayInstane(serviceId, instanceId);
        if (grayInstance != null) {
            grayInstance.removeGrayPolicy(policyGroupId, policyId);
        }
    }

    @Override
    public void addGrayPolicyGroup(String serviceId, String instanceId, GrayPolicyGroup policyGroup) {
        GrayInstance grayInstance = getGrayInstane(serviceId, instanceId);
        if (grayInstance != null) {
            grayInstance.addGrayPolicyGroup(policyGroup);
        }
    }

    @Override
    public void deleteGrayPolicyGroup(String serviceId, String instanceId, String policyGroupId) {
        GrayInstance grayInstance = getGrayInstane(serviceId, instanceId);
        if (grayInstance != null) {
            grayInstance.removeGrayPolicyGroup(policyGroupId);
        }
    }


    @Override
    public Collection<GrayService> allGrayService() {
        List<GrayService> grayServiceList = new ArrayList<>();
        //为了信息准确，从eureka获取服务列表
        List<GrayServiceVO> list = grayService.services();
        if (CollectionUtils.isEmpty(list)) {
            return grayServiceList;
        }
        for (GrayServiceVO vo : list) {
            GrayService grayService = getGrayService(vo.getServiceId());
            if (grayService != null) {//注意判空
                grayServiceList.add(grayService);
            }
        }
        return grayServiceList;
    }

    @Override
    public GrayService getGrayService(String serviceId) {
        //为了信息准确，从eureka获取服务的实例
        List<GrayInstanceVO> grayInstanceVOList = grayService.instances(serviceId);
        if (CollectionUtils.isEmpty(grayInstanceVOList)) {
            return null;
        }
        List<GrayInstance> grayInstanceList = new ArrayList<>();
        grayInstanceVOList.stream().forEach(e -> {
            List<GrayPolicyGroup> grayPolicyGroups = getPolicyGroups(e.getInstanceId());
            GrayInstance grayInstance = new GrayInstance();
            grayInstance.setInstanceId(e.getInstanceId());
            grayInstance.setOpenGray(e.isOpenGray());
            grayInstance.setServiceId(serviceId);
            grayInstance.setGrayPolicyGroups(grayPolicyGroups);
            grayInstanceList.add(grayInstance);
        });
        GrayService grayService = new GrayService(serviceId, grayInstanceList);
        return grayService;
    }


    @Override
    public GrayInstance getGrayInstane(String serviceId, String instanceId) {
        GrayService grayService = getGrayService(serviceId);
        if (grayService != null) {
            return grayService.getGrayInstance(instanceId);
        }
        return null;
    }

    @Override
    public List<GrayPolicyGroup> getPolicyGroups(String instanceId) {
        List<GrayPolicyGroupEntity> grayPolicyGroupEntities = grayInstancePolicyGroupMapper.selectPolicyGroupByInstanceId(instanceId);
        if (CollectionUtils.isEmpty(grayPolicyGroupEntities)) {
            return new ArrayList<>();
        }
        List<GrayPolicyGroup> grayPolicyGroups = new ArrayList<>();
        grayPolicyGroupEntities.stream().forEach(f -> {
            GrayPolicyGroup grayPolicyGroup = new GrayPolicyGroup();
            grayPolicyGroup.setPolicyGroupId(f.getPolicyGroupId());
            grayPolicyGroup.setAlias(f.getAlias());
            grayPolicyGroup.setEnable(f.getEnable() == 0 ? false : true);
            f.getGrayPolicyEntities().stream().forEach(m -> {
                GrayPolicy grayPolicy = new GrayPolicy();
                grayPolicy.setPolicyId(m.getPolicyId());
                grayPolicy.setPolicyType(m.getPolicyType());
                //TODO
                grayPolicy.setInfos(new HashMap<>());
                grayPolicyGroup.addGrayPolicy(grayPolicy);
            });
            grayPolicyGroups.add(grayPolicyGroup);
        });
        return grayPolicyGroups;
    }

    @Override
    public boolean updateInstanceStatus(String serviceId, String instanceId, int status) {
        GrayInstance grayInstance = getGrayInstane(serviceId, instanceId);
        if (grayInstance == null) {
            grayInstance = new GrayInstance();
            grayInstance.setServiceId(serviceId);
            grayInstance.setInstanceId(instanceId);
        }
        addGrayInstance(grayInstance);//持久化服务实例
        grayInstance.setOpenGray(status == 1);
        GrayInstanceEntity entity = new GrayInstanceEntity();
        entity.setInstanceId(instanceId);
        entity.setOpenGray(status);
        //把灰度状态持久化
        int r = grayInstanceMapper.updateByInstanceId(entity);
        logger.info("更新服务实例灰度状态serviceId:{},instanceId:{},r:{}", serviceId, instanceId, r);
        return true;
    }

    @Override
    public boolean updatePolicyGroupStatus(String serviceId, String instanceId, String groupId, int enable) {
        GrayInstance grayInstance = getGrayInstane(serviceId, instanceId);
        if (grayInstance != null) {
            GrayPolicyGroup policyGroup = grayInstance.getGrayPolicyGroup(groupId);
            if (policyGroup != null) {
                policyGroup.setEnable(enable == 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public void openForWork() {
        evictionTimer.schedule(new EvictionTask(),
                serverConfig.getEvictionIntervalTimerInMs(),
                serverConfig.getEvictionIntervalTimerInMs());
    }

    @Override
    public void shutdown() {
        evictionTimer.cancel();
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
