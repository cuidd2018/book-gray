package cn.dingyuegroup.gray.server.service.impl;

import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayPolicyGroup;
import cn.dingyuegroup.gray.core.GrayServiceManager;
import cn.dingyuegroup.gray.server.model.vo.GrayInstanceVO;
import cn.dingyuegroup.gray.server.model.vo.GrayPolicyGroupVO;
import cn.dingyuegroup.gray.server.mysql.dao.GrayInstanceMapper;
import cn.dingyuegroup.gray.server.mysql.dao.GrayServiceMapper;
import cn.dingyuegroup.gray.server.mysql.entity.GrayInstanceEntity;
import cn.dingyuegroup.gray.server.mysql.entity.GrayServiceEntity;
import cn.dingyuegroup.gray.server.service.AbstractGrayService;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EurekaGrayService extends AbstractGrayService {

    private final EurekaClient eurekaClient;
    private final DiscoveryClient discoveryClient;
    private final GrayServiceManager grayServiceManager;
    private final GrayServiceMapper grayServiceMapper;

    public EurekaGrayService(EurekaClient eurekaClient, DiscoveryClient discoveryClient, GrayServiceManager
            grayServiceManager, GrayServiceMapper grayServiceMapper) {
        super(grayServiceManager, discoveryClient, eurekaClient, grayServiceMapper);
        this.eurekaClient = eurekaClient;
        this.discoveryClient = discoveryClient;
        this.grayServiceManager = grayServiceManager;
        this.grayServiceMapper = grayServiceMapper;
    }

    @Autowired
    private GrayInstanceMapper grayInstanceMapper;

    /**
     * 返回服务实例列表
     *
     * @param serviceId 服务id
     * @return 灰度服务实例VO列表
     */
    @Override
    public List<GrayInstanceVO> instances(String serviceId) {
        List<GrayInstanceVO> list = new ArrayList<>();
        GrayServiceEntity entity = grayServiceMapper.selectByServiceId(serviceId);
        //从eureka获取服务实例详细信息
        Application app = eurekaClient.getApplication(serviceId);
        List<InstanceInfo> instanceInfos = app.getInstances();
        for (InstanceInfo instanceInfo : instanceInfos) {
            GrayInstanceVO vo = new GrayInstanceVO();
            if (entity != null) {
                vo.setAppName(entity.getAppName());
            }
            vo.setServiceId(serviceId);
            vo.setInstanceId(instanceInfo.getInstanceId());
            vo.setMetadata(instanceInfo.getMetadata());
            vo.setUrl(instanceInfo.getHomePageUrl());
            vo.setOpenGray(true);
            GrayInstanceEntity grayInstanceEntity = grayInstanceMapper.selectByInstanceId(instanceInfo.getInstanceId());
            if (grayInstanceEntity != null) {
                vo.setOpenGray(grayInstanceEntity.getOpenGray() == 0 ? false : true);
                //vo.setHasGrayPolicies(grayInstance.hasGrayPolicy());
            }
            list.add(vo);
        }
        return list;
    }

    /**
     * 校验服务实例是否存在
     *
     * @param instanceId
     * @return
     */
    @Override
    public boolean vertifyInstance(String serviceId, String instanceId) {
        List<GrayInstanceVO> list = instances(serviceId);
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        GrayInstanceVO vo = list.stream().filter(e -> e.getInstanceId().equals(instanceId)).findFirst().get();
        if (vo != null) {
            return true;
        }
        return false;
    }


    /**
     * 服务实例的所有灰度策略组
     *
     * @param serviceId  服务id
     * @param instanceId 实例id
     * @return 灰策略组VO列表
     */
    @Override
    public ResponseEntity<List<GrayPolicyGroupVO>> policyGroups(String serviceId, String instanceId) {

        Application app = eurekaClient.getApplication(serviceId);
        InstanceInfo instanceInfo = app.getByInstanceId(instanceId);
        GrayInstance grayInstance = grayServiceManager.getGrayInstane(serviceId, instanceId);
        String appName = instanceInfo.getAppName();
        String homePageUrl = instanceInfo.getHomePageUrl();
        if (grayInstance != null && grayInstance.getGrayPolicyGroups() != null) {
            List<GrayPolicyGroup> policyGroups = grayInstance.getGrayPolicyGroups();
            List<GrayPolicyGroupVO> vos = new ArrayList<>(policyGroups.size());
            for (GrayPolicyGroup policyGroup : policyGroups) {
                vos.add(getPolicyGroup(serviceId, appName, instanceId, homePageUrl, policyGroup));
            }
            return ResponseEntity.ok(vos);
        }
        return ResponseEntity.ok(Collections.emptyList());
    }


    /**
     * 灰度策略组
     *
     * @param serviceId  服务id
     * @param instanceId 实例id
     * @param groupId    灰度策略组id
     * @return 灰度策略组VO
     */
    @Override
    public ResponseEntity<GrayPolicyGroupVO> policyGroup(String serviceId, String instanceId, String groupId) {
        Application app = eurekaClient.getApplication(serviceId);
        InstanceInfo instanceInfo = app.getByInstanceId(instanceId);
        GrayInstance grayInstance = grayServiceManager.getGrayInstane(serviceId, instanceId);
        String appName = instanceInfo.getAppName();
        String homePageUrl = instanceInfo.getHomePageUrl();
        if (grayInstance != null) {
            GrayPolicyGroup policyGroup = grayInstance.getGrayPolicyGroup(groupId);
            if (policyGroup != null) {
                return ResponseEntity.ok(getPolicyGroup(serviceId, appName, instanceId, homePageUrl, policyGroup));
            }
        }
        return ResponseEntity.ok().build();
    }
}
