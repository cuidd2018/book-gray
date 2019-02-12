package cn.dingyuegroup.gray.server.web;

import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayService;
import cn.dingyuegroup.gray.server.manager.GrayServiceManager;
import cn.dingyuegroup.gray.server.model.vo.GrayInstanceVO;
import cn.dingyuegroup.gray.server.model.vo.GrayPolicyGroupVO;
import cn.dingyuegroup.gray.server.model.vo.GrayServiceVO;
import cn.dingyuegroup.gray.server.mysql.entity.GrayRbacResources;
import cn.dingyuegroup.gray.server.refresh.RefreshGrayClient;
import cn.dingyuegroup.gray.server.vertify.VertifyRequest;
import cn.dingyuegroup.gray.server.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/gray/manager/services")
public class GrayServiceController extends BaseController {

    @Autowired
    private GrayServiceManager grayServiceManager;

    @RequestMapping("/index")
    public ModelAndView index(ModelAndView model) {
        List<GrayServiceVO> list = new ArrayList<>();
        List<GrayService> allservices = grayServiceManager.getServices();
        List<GrayRbacResources> grayRbacResources = getResources();
        allservices = allservices.stream().filter(e -> hasAuth(e, grayRbacResources)).collect(Collectors.toList());
        allservices.stream().forEach(e -> {
            GrayServiceVO vo = GrayServiceVO.builder()
                    .appName(e.getAppName())
                    .status(e.isStatus())
                    .serviceId(e.getServiceId())
                    .remark(e.getRemark())
                    .build();
            vo.setInstanceSize(e.getGrayInstances().size());
            vo.setHasGrayInstances(e.isOpenGray());
            vo.setHasGrayPolicies(e.hasGrayPolicy());
            list.add(vo);
        });
        model.addObject("list", list);
        model.setViewName("gray/service");
        return model;
    }

    @RefreshGrayClient
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addService(@RequestParam String appName, @RequestParam String serviceId, @RequestParam String remark) {
        grayServiceManager.addService(appName, serviceId, remark);
        return "redirect:/gray/manager/services/index";
    }

    @RefreshGrayClient
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String editService(@RequestParam String appName, @RequestParam String serviceId, @RequestParam String remark) {
        grayServiceManager.editService(appName, serviceId, remark);
        return "redirect:/gray/manager/services/index";
    }

    @RefreshGrayClient
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteService(@RequestParam String serviceId) {
        grayServiceManager.deleteService(serviceId);
        return "redirect:/gray/manager/services/index";
    }

    /**
     * 返回服务实例列表
     *
     * @param serviceId 服务id
     * @return 灰度服务实例VO列表
     */
    @RequestMapping(value = "/instances/index")
    public ModelAndView instances(ModelAndView model, @RequestParam("serviceId") String serviceId) {
        List<GrayInstanceVO> list = new ArrayList<>();
        List<GrayInstance> allInstances = grayServiceManager.getInstances(serviceId);
        List<GrayRbacResources> grayRbacResources = getResources();
        List<String> resources = grayRbacResources.stream().map(GrayRbacResources::getResource).collect(Collectors.toList());
        allInstances.stream().forEach(e -> {
            if (!resources.contains(e.getEnv())) {
                return;
            }
            GrayInstanceVO vo = GrayInstanceVO.builder()
                    .serviceId(serviceId)
                    .instanceId(e.getInstanceId())
                    .status(e.isStatus())
                    .appName(e.getAppName())
                    .url(e.getUrl())
                    .metadata(e.getMetadata())
                    .openGray(e.isOpenGray())
                    .hasGrayPolicies(e.hasGrayPolicy())
                    .remark(e.getRemark())
                    .policyGroupId(e.getGrayPolicyGroup() == null ? null : e.getGrayPolicyGroup().getPolicyGroupId())
                    .policyGroupAlias(e.getGrayPolicyGroup() == null ? null : e.getGrayPolicyGroup().getAlias())
                    .envName(e.getEnvName())
                    .build();
            list.add(vo);
        });
        model.addObject("list", list);
        model.setViewName("gray/instance");
        return model;
    }

    @RefreshGrayClient
    @RequestMapping(value = "/instances/edit", method = RequestMethod.POST)
    public String editInstances(RedirectAttributes attr, @RequestParam("serviceId") String serviceId, @RequestParam("instanceId") String instanceId, @RequestParam("remark") String remark) {
        grayServiceManager.editInstance(serviceId, instanceId, remark);
        attr.addAttribute("serviceId", serviceId);
        return "redirect:/gray/manager/services/instances/index";
    }

    @RefreshGrayClient
    @RequestMapping(value = "/instances/delete", method = RequestMethod.POST)
    public String deleteInstances(RedirectAttributes attr, @RequestParam("serviceId") String serviceId, @RequestParam("instanceId") String instanceId) {
        grayServiceManager.deleteInstance(serviceId, instanceId);
        attr.addAttribute("serviceId", serviceId);
        return "redirect:/gray/manager/services/instances/index";
    }

    @RefreshGrayClient
    @RequestMapping(value = "/instance/grayStatus", method = RequestMethod.POST)
    public String editInstanceGrayStatus(RedirectAttributes attr,
                                         @RequestParam("serviceId") String serviceId,
                                         @RequestParam("instanceId") String instanceId,
                                         @RequestParam("status") int status) {
        grayServiceManager.editInstanceGrayStatus(serviceId, instanceId, status);
        attr.addAttribute("serviceId", serviceId);
        return "redirect:/gray/manager/services/instances/index";
    }

    @RefreshGrayClient
    @RequestMapping(value = "/instance/onlineStatus", method = RequestMethod.POST)
    public String editInstanceOnlineStatus(RedirectAttributes attr,
                                           @RequestParam("serviceId") String serviceId,
                                           @RequestParam("instanceId") String instanceId,
                                           @RequestParam("status") int status) {
        boolean b = grayServiceManager.editInstanceOnlineStatus(serviceId, instanceId, status);
        attr.addAttribute("serviceId", serviceId);
        return "redirect:/gray/manager/services/instances/index";
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

    /**
     * 服务实例添加策略组
     *
     * @param serviceId 服务id
     * @return Void
     */
    @RefreshGrayClient
    @RequestMapping(value = "/instance/policyGroup/relate", method = RequestMethod.POST)
    public String addPolicyGroup(RedirectAttributes attr,
                                 @RequestParam("serviceId") String serviceId, @RequestParam("instanceId") String instanceId,
                                 @RequestParam(value = "policyGroupId", required = false) String groupId) {
        if (StringUtils.isEmpty(groupId) || !groupId.contains("POLICY_GROUP")) {
            grayServiceManager.delInstancePolicyGroup(serviceId, instanceId, null);
        } else {
            grayServiceManager.editInstancePolicyGroup(serviceId, instanceId, groupId);
        }
        attr.addAttribute("serviceId", serviceId);
        return "redirect:/gray/manager/services/instances/index";
    }

    private boolean hasAuth(GrayService grayService, List<GrayRbacResources> resourceList) {
        if (grayService == null || CollectionUtils.isEmpty(grayService.getGrayInstances()) || CollectionUtils.isEmpty(resourceList)) {
            return false;
        }
        List<String> resources = resourceList.stream().map(GrayRbacResources::getResource).collect(Collectors.toList());
        Optional<GrayInstance> optional = grayService.getGrayInstances().parallelStream().filter(e -> resources.contains(e.getEnv())).findAny();
        if (optional.isPresent()) {
            return true;
        }
        return false;
    }
}
