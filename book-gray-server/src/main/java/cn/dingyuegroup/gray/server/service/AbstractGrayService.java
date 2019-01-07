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
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        //获取持久化的服务信息
        List<GrayServiceEntity> entityList = grayServiceMapper.selectAll();
        entityList.stream().forEach(e -> {
            GrayServiceVO vo = GrayServiceVO.builder()
                    .appName(e.getAppName())
                    .status(false)//默认是不在线的
                    .serviceId(e.getServiceId())
                    .build();
            services.add(vo);
        });
        //从eureka获取在线服务
        List<String> upServiceIds = discoveryClient.getServices();
        upServiceIds.stream().forEach(e -> {
            Optional<GrayServiceVO> optional = services.parallelStream().filter(f -> f.getServiceId().equals(e)).findAny();
            if (optional.isPresent()) {
                GrayServiceVO vo = optional.get();
                vo.setStatus(true);
            } else {
                GrayServiceVO vo = GrayServiceVO.builder()
                        .serviceId(e)
                        .status(true)
                        .build();
                services.add(vo);
            }
        });

        for (GrayServiceVO grayServiceVO : services) {
            GrayService grayService = grayServiceManager.getGrayService(grayServiceVO.getServiceId());
            if (grayService != null) {
                grayServiceVO.setInstanceSize(grayService.getGrayInstances().size());
                grayServiceVO.setHasGrayInstances(grayService.isOpenGray());
                grayServiceVO.setHasGrayPolicies(grayService.hasGrayPolicy());
            }
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


    protected GrayPolicyGroupVO getPolicyGroup(String serviceId, String appName, String instanceId, GrayPolicyGroup policyGroup) {
        GrayPolicyGroupVO vo = new GrayPolicyGroupVO();
        vo.setAppName(appName);
        vo.setInstanceId(instanceId);
        vo.setServiceId(serviceId);
        vo.setAlias(policyGroup.getAlias());
        vo.setPolicyGroupId(policyGroup.getPolicyGroupId());
        vo.setPolicies(policyGroup.getList());
        vo.setEnable(policyGroup.isEnable());
        return vo;
    }

}
