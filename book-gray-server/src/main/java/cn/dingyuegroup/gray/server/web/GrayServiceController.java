package cn.dingyuegroup.gray.server.web;

import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayPolicyGroup;
import cn.dingyuegroup.gray.core.GrayService;
import cn.dingyuegroup.gray.server.manager.GrayServiceManager;
import cn.dingyuegroup.gray.server.model.vo.GrayInstanceVO;
import cn.dingyuegroup.gray.server.model.vo.GrayPolicyGroupVO;
import cn.dingyuegroup.gray.server.model.vo.GrayServiceVO;
import cn.dingyuegroup.gray.server.vertify.VertifyRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gray/manager/services")
public class GrayServiceController {
    @Autowired
    private GrayServiceManager grayServiceManager;


    /**
     * 返回所有服务
     *
     * @return 灰度服务VO集合
     */
    @RequestMapping(value = "")
    public ResponseEntity<List<GrayServiceVO>> services() {
        List<GrayServiceVO> list = new ArrayList<>();
        List<GrayService> grayServices = grayServiceManager.getServices();
        grayServices.stream().forEach(e -> {
            GrayServiceVO vo = GrayServiceVO.builder()
                    .appName(e.getAppName())
                    .status(e.isStatus())
                    .serviceId(e.getServiceId())
                    .build();
            vo.setInstanceSize(e.getGrayInstances().size());
            vo.setHasGrayInstances(e.isOpenGray());
            vo.setHasGrayPolicies(e.hasGrayPolicy());
            list.add(vo);
        });
        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "更新服务在线状态")
    @VertifyRequest
    @RequestMapping(value = "/onlineStatus", method = RequestMethod.GET)
    public ResponseEntity<Void> editServiceOnlineStatus(
            @RequestParam("serviceId") String serviceId,
            @ApiParam("0:关闭, 1:启用") @RequestParam("status") int status) {
        boolean b = grayServiceManager.editGrayServiceOnlineStatus(serviceId, status);
        if (b) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * 返回服务实例列表
     *
     * @param serviceId 服务id
     * @return 灰度服务实例VO列表
     */
    @VertifyRequest
    @RequestMapping(value = "/instances", method = RequestMethod.GET)
    public ResponseEntity<List<GrayInstanceVO>> instances(@RequestParam("serviceId") String serviceId) {
        List<GrayInstanceVO> list = new ArrayList<>();
        List<GrayInstance> grayInstances = grayServiceManager.getInstances(serviceId);
        grayInstances.stream().forEach(e -> {
            GrayInstanceVO vo = GrayInstanceVO.builder()
                    .serviceId(serviceId)
                    .instanceId(e.getInstanceId())
                    .status(e.isStatus())
                    .appName(e.getAppName())
                    .url(e.getUrl())
                    .metadata(e.getMetadata())
                    .openGray(e.isOpenGray())
                    .hasGrayPolicies(e.hasGrayPolicy())
                    .build();
            list.add(vo);
        });
        return ResponseEntity.ok(list);
    }


    @ApiOperation(value = "更新实例灰度状态")
    @VertifyRequest
    @RequestMapping(value = "/instance/grayStatus", method = RequestMethod.GET)
    public ResponseEntity<Void> editInstanceGrayStatus(
            @RequestParam("serviceId") String serviceId,
            @RequestParam("instanceId") String instanceId,
            @ApiParam("0:关闭, 1:启用") @RequestParam("status") int status) {
        boolean b = grayServiceManager.editInstanceGrayStatus(serviceId, instanceId, status);
        if (b) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @ApiOperation(value = "更新实例灰度状态")
    @VertifyRequest
    @RequestMapping(value = "/instance/onlineStatus", method = RequestMethod.GET)
    public ResponseEntity<Void> editInstanceOnlineStatus(
            @RequestParam("serviceId") String serviceId,
            @RequestParam("instanceId") String instanceId,
            @ApiParam("0:关闭, 1:启用") @RequestParam("status") int status) {
        boolean b = grayServiceManager.editInstanceOnlineStatus(serviceId, instanceId, status);
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
    @VertifyRequest
    @RequestMapping(value = "/instance/policyGroups", method = RequestMethod.GET)
    public ResponseEntity<List<GrayPolicyGroupVO>> policyGroups(@RequestParam("serviceId") String serviceId,
                                                                @RequestParam("instanceId") String instanceId) {
        GrayInstance grayInstance = grayServiceManager.getGrayInstance(serviceId, instanceId);
        if (grayInstance == null) {
            return ResponseEntity.ok().build();
        }
        List<GrayPolicyGroupVO> grayPolicyGroupVOS = new ArrayList<>();
        grayInstance.getGrayPolicyGroups().stream().forEach(e -> {
            GrayPolicyGroupVO vo = new GrayPolicyGroupVO();
            vo.setServiceId(serviceId);
            vo.setInstanceId(instanceId);
            vo.setAppName(grayInstance.getAppName());
            vo.setPolicyGroupId(e.getPolicyGroupId());
            vo.setAlias(e.getAlias());
            vo.setPolicies(e.getList());
            vo.setEnable(e.isEnable());
            grayPolicyGroupVOS.add(vo);
        });
        return ResponseEntity.ok(grayPolicyGroupVOS);
    }


    /**
     * 灰度策略组
     *
     * @param serviceId  服务id
     * @param instanceId 实例id
     * @param groupId    灰度策略组id
     * @return 灰度策略组VO
     */
    @RequestMapping(value = "/instance/policyGroup", method = RequestMethod.GET)
    public ResponseEntity<GrayPolicyGroupVO> policyGroup(@RequestParam("serviceId") String serviceId,
                                                         @RequestParam("instanceId") String instanceId,
                                                         @RequestParam("groupId") String groupId) {
        GrayInstance grayInstance = grayServiceManager.getGrayInstance(serviceId, instanceId);
        if (grayInstance == null || grayInstance.getGrayPolicyGroups() == null) {
            return ResponseEntity.ok().build();
        }
        Optional<GrayPolicyGroup> optional = grayInstance.getGrayPolicyGroups().stream().filter(e -> e.getPolicyGroupId().equals(groupId)).findAny();
        GrayPolicyGroupVO vo = new GrayPolicyGroupVO();
        if (optional.isPresent()) {
            GrayPolicyGroup grayPolicyGroup = optional.get();
            vo.setServiceId(serviceId);
            vo.setInstanceId(instanceId);
            vo.setAppName(grayInstance.getAppName());
            vo.setPolicyGroupId(grayPolicyGroup.getPolicyGroupId());
            vo.setAlias(grayPolicyGroup.getAlias());
            vo.setPolicies(grayPolicyGroup.getList());
            vo.setEnable(grayPolicyGroup.isEnable());
        }
        return ResponseEntity.ok(vo);
    }


    @ApiOperation(value = "更新实例策略组启用状态")
    @RequestMapping(value = "/instance/policyGroup/status", method = RequestMethod.GET)
    public ResponseEntity<Void> editPolicyGroupStatus(@RequestParam("serviceId") String serviceId,
                                                      @RequestParam("instanceId") String instanceId,
                                                      @RequestParam("groupId") String groupId,
                                                      @ApiParam("0:关闭, 1:启用") @RequestParam("status") int enable) {
        boolean b = grayServiceManager.editPolicyGroupStatus(serviceId, instanceId, groupId, enable);
        if (b) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }


    /**
     * 服务实例添加策略组
     *
     * @param serviceId 服务id
     * @return Void
     */
    @RequestMapping(value = "/instance/policyGroup/relate", method = RequestMethod.GET)
    public ResponseEntity<Void> addPolicyGroup(
            @RequestParam("serviceId") String serviceId, @RequestParam("instanceId") String instanceId,
            @RequestParam("groupId") String groupId) {
        boolean b = grayServiceManager.addInstancePolicyGroup(serviceId, instanceId, groupId);
        if (b) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }


    /**
     * 服务实例删除策略组
     *
     * @param serviceId     服务id
     * @param instanceId    实例id
     * @param policyGroupId 灰度策略组id
     * @return Void
     */
    @ApiOperation("删除策略组")
    @RequestMapping(value = "/instance/policyGroup/unRelate", method = RequestMethod.GET)
    public ResponseEntity<Void> delPolicyGroup(
            @RequestParam("serviceId") String serviceId,
            @RequestParam("instanceId") String instanceId,
            @RequestParam("groupId") String policyGroupId) {
        boolean b = grayServiceManager.delInstancePolicyGroup(serviceId, instanceId, policyGroupId);
        if (b) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}