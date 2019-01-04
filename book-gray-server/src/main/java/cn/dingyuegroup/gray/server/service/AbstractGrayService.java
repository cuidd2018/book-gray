package cn.dingyuegroup.gray.server.service;

import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayPolicyGroup;
import cn.dingyuegroup.gray.core.GrayService;
import cn.dingyuegroup.gray.core.GrayServiceManager;
import cn.dingyuegroup.gray.server.model.fo.GrayPolicyGroupFO;
import cn.dingyuegroup.gray.server.model.vo.GrayInstanceVO;
import cn.dingyuegroup.gray.server.model.vo.GrayPolicyGroupVO;
import cn.dingyuegroup.gray.server.model.vo.GrayServiceVO;
import cn.dingyuegroup.gray.server.mysql.dao.GrayServiceMapper;
import cn.dingyuegroup.gray.server.mysql.entity.GrayServiceEntity;
import com.netflix.discovery.EurekaClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGrayService {

    private GrayServiceManager grayServiceManager;
    private DiscoveryClient discoveryClient;
    private EurekaClient eurekaClient;
    private GrayServiceMapper grayServiceMapper;

    public AbstractGrayService(GrayServiceManager grayServiceManager, DiscoveryClient discoveryClient,
                               EurekaClient eurekaClient, GrayServiceMapper grayServiceMapper) {
        this.grayServiceManager = grayServiceManager;
        this.discoveryClient = discoveryClient;
        this.eurekaClient = eurekaClient;
        this.grayServiceMapper = grayServiceMapper;
    }

    /**
     * 返回所有服务
     *
     * @return 灰度服务VO集合
     */
    public List<GrayServiceVO> services() {
        List<GrayServiceVO> services = new ArrayList<>();
        //从eureka获取服务
        List<String> serviceIds = discoveryClient.getServices();
        if (CollectionUtils.isEmpty(serviceIds)) {
            return services;
        }
        //获取各服务自定义配置信息
        List<GrayServiceEntity> entityList = grayServiceMapper.selectAll();
        for (String serviceId : serviceIds) {
            //获取每个服务的服务实例
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
            if (null == instances || instances.isEmpty()) {
                continue;
            }
            GrayServiceVO vo = new GrayServiceVO();
            for (GrayServiceEntity entity : entityList) {
                if (!StringUtils.isEmpty(entity.getServiceId()) && entity.getServiceId().equals(serviceId)) {
                    vo.setAppName(entity.getAppName());
                    break;
                }
            }
            vo.setServiceId(serviceId);
            vo.setInstanceSize(instances.size());
            GrayService grayService = grayServiceManager.getGrayService(serviceId);
            if (grayService != null) {
                vo.setHasGrayInstances(grayService.isOpenGray());
                vo.setHasGrayPolicies(grayService.hasGrayPolicy());
            }
            services.add(vo);
        }
        return services;
    }

    /**
     * 返回服务实例列表
     *
     * @param serviceId 服务id
     * @return 灰度服务实例VO列表
     */
    public abstract List<GrayInstanceVO> instances(String serviceId);

    /**
     * 校验服务是否存在
     *
     * @param serviceId
     * @return
     */
    public boolean vertifyService(String serviceId) {
        GrayServiceEntity entity = grayServiceMapper.selectByServiceId(serviceId);
        if (entity != null) {
            return true;
        }
        return false;
    }

    /**
     * 校验服务实例是否存在
     *
     * @param instanceId
     * @return
     */
    public abstract boolean vertifyInstance(String serviceId, String instanceId);

    public ResponseEntity<Void> editInstanceStatus(String serviceId, String instanceId, int status) {
        boolean b = grayServiceManager.updateInstanceStatus(serviceId, instanceId, status);
        if (b) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }


    /**
     * 服务实例的所有灰度策略组
     *
     * @param serviceId  服务id
     * @param instanceId 实例id
     * @return 灰策略组VO列表
     */
    public abstract ResponseEntity<List<GrayPolicyGroupVO>> policyGroups(String serviceId, String instanceId);


    /**
     * 灰度策略组
     *
     * @param serviceId  服务id
     * @param instanceId 实例id
     * @param groupId    灰度策略组id
     * @return 灰度策略组VO
     */
    public abstract ResponseEntity<GrayPolicyGroupVO> policyGroup(String serviceId, String instanceId, String groupId);


    public ResponseEntity<Void> editPolicyGroupStatus(String serviceId, String instanceId, String groupId, int enable) {

        return grayServiceManager.updatePolicyGroupStatus(serviceId, instanceId, groupId, enable)
                ? ResponseEntity.ok().build() :
                ResponseEntity.badRequest().build();
    }


    /**
     * 添加策略组
     *
     * @param serviceId     服务id
     * @param policyGroupFO 灰度策略组FO
     * @return Void
     */
    public ResponseEntity<Void> policyGroup(String serviceId, GrayPolicyGroupFO policyGroupFO) {
        GrayInstance grayInstance = grayServiceManager.getGrayInstane(serviceId, policyGroupFO.getInstanceId());
        if (grayInstance == null) {
            grayInstance = new GrayInstance();
            grayInstance.setServiceId(serviceId);
            grayInstance.setInstanceId(policyGroupFO.getInstanceId());
            grayServiceManager.addGrayInstance(grayInstance);
            grayInstance = grayServiceManager.getGrayInstane(serviceId, policyGroupFO.getInstanceId());
        }

        grayInstance.addGrayPolicyGroup(policyGroupFO.toGrayPolicyGroup());

        return ResponseEntity.ok().build();
    }


    /**
     * 删除策略组
     *
     * @param serviceId     服务id
     * @param instanceId    实例id
     * @param policyGroupId 灰度策略组id
     * @return Void
     */
    public ResponseEntity<Void> delPolicyGroup(String serviceId, String instanceId, String policyGroupId) {
        GrayInstance grayInstance = grayServiceManager.getGrayInstane(serviceId, instanceId);
        if (grayInstance != null) {
            if (grayInstance.removeGrayPolicyGroup(policyGroupId) != null && grayInstance.getGrayPolicyGroups()
                    .isEmpty()) {
                grayServiceManager.deleteGrayInstance(serviceId, instanceId);
            }
        }

        return ResponseEntity.ok().build();
    }


    protected GrayPolicyGroupVO getPolicyGroup(String serviceId, String appName, String instanceId, String homePageUrl,
                                               GrayPolicyGroup policyGroup) {
        GrayPolicyGroupVO vo = new GrayPolicyGroupVO();
        vo.setAppName(appName);
        vo.setInstanceId(instanceId);
        vo.setServiceId(serviceId);
        vo.setUrl(homePageUrl);
        vo.setAlias(policyGroup.getAlias());
        vo.setPolicyGroupId(policyGroup.getPolicyGroupId());
        vo.setPolicies(policyGroup.getList());
        vo.setEnable(policyGroup.isEnable());
        return vo;
    }

}
