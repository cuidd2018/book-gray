package cn.dingyuegroup.gray.server.web;

import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayService;
import cn.dingyuegroup.gray.server.manager.GrayServiceManager;
import cn.dingyuegroup.gray.server.manager.RbacManager;
import cn.dingyuegroup.gray.server.model.vo.GrayInstanceVO;
import cn.dingyuegroup.gray.server.model.vo.GrayPolicyGroupVO;
import cn.dingyuegroup.gray.server.model.vo.GrayServiceVO;
import cn.dingyuegroup.gray.server.vertify.VertifyRequest;
import cn.dingyuegroup.gray.server.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/gray/manager/services")
public class GrayServiceController extends BaseController {

    @Autowired
    private GrayServiceManager grayServiceManager;

    @Autowired
    private RbacManager rbacManager;

    @RequestMapping("/index")
    public ModelAndView index(ModelAndView model) {
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
        model.addObject("list", list);
        model.setViewName("gray/gray");
        return model;
    }

    @RequestMapping("/add")
    public String addService(Model model, @RequestParam String appName, @RequestParam String serviceId, @RequestParam String remark) {
        grayServiceManager.addService(appName, serviceId, remark);
        return "redirect:/gray/manager/services/index";
    }


    /**
     * 返回服务实例列表
     *
     * @param serviceId 服务id
     * @return 灰度服务实例VO列表
     */
    @VertifyRequest
    @ResponseBody
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

    @VertifyRequest
    @ResponseBody
    @RequestMapping(value = "/instance/grayStatus", method = RequestMethod.GET)
    public ResponseEntity<Void> editInstanceGrayStatus(
            @RequestParam("serviceId") String serviceId,
            @RequestParam("instanceId") String instanceId,
            @RequestParam("status") int status) {
        boolean b = grayServiceManager.editInstanceGrayStatus(serviceId, instanceId, status);
        if (b) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @VertifyRequest
    @ResponseBody
    @RequestMapping(value = "/instance/onlineStatus", method = RequestMethod.GET)
    public ResponseEntity<Void> editInstanceOnlineStatus(
            @RequestParam("serviceId") String serviceId,
            @RequestParam("instanceId") String instanceId,
            @RequestParam("status") int status) {
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
    @ResponseBody
    @RequestMapping(value = "/instance/policyGroup", method = RequestMethod.GET)
    public ResponseEntity<GrayPolicyGroupVO> policyGroup(@RequestParam("serviceId") String serviceId,
                                                         @RequestParam("instanceId") String instanceId) {
        GrayInstance grayInstance = grayServiceManager.getGrayInstance(serviceId, instanceId);
        if (grayInstance == null) {
            return ResponseEntity.ok().build();
        }
        GrayPolicyGroupVO vo = new GrayPolicyGroupVO();
        vo.setServiceId(serviceId);
        vo.setInstanceId(instanceId);
        vo.setAppName(grayInstance.getAppName());
        if (grayInstance.getGrayPolicyGroup() != null) {
            vo.setPolicyGroupId(grayInstance.getGrayPolicyGroup().getPolicyGroupId());
            vo.setAlias(grayInstance.getGrayPolicyGroup().getAlias());
            vo.setPolicies(grayInstance.getGrayPolicyGroup().getList());
            vo.setEnable(grayInstance.getGrayPolicyGroup().isEnable());
        }
        return ResponseEntity.ok(vo);
    }

    @ResponseBody
    @RequestMapping(value = "/instance/policyGroup/status", method = RequestMethod.GET)
    public ResponseEntity<Void> editPolicyGroupStatus(@RequestParam("serviceId") String serviceId,
                                                      @RequestParam("instanceId") String instanceId,
                                                      @RequestParam("groupId") String groupId,
                                                      @RequestParam("status") int enable) {
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
    @ResponseBody
    public ResponseEntity<Void> addPolicyGroup(
            @RequestParam("serviceId") String serviceId, @RequestParam("instanceId") String instanceId,
            @RequestParam("groupId") String groupId) {
        boolean b = grayServiceManager.editInstancePolicyGroup(serviceId, instanceId, groupId);
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
    @ResponseBody
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
