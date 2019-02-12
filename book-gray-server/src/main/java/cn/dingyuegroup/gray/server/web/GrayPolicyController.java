package cn.dingyuegroup.gray.server.web;

import cn.dingyuegroup.gray.server.manager.GrayServiceManager;
import cn.dingyuegroup.gray.server.model.resp.RespMsg;
import cn.dingyuegroup.gray.server.model.vo.GrayPolicyGroupVO;
import cn.dingyuegroup.gray.server.model.vo.GrayPolicyVO;
import cn.dingyuegroup.gray.server.mysql.dao.GrayPolicyGroupMapper;
import cn.dingyuegroup.gray.server.mysql.entity.GrayPolicyGroupEntity;
import cn.dingyuegroup.gray.server.refresh.RefreshGrayClient;
import cn.dingyuegroup.gray.server.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 170147 on 2019/1/8.
 */
@Controller
@RequestMapping("/gray/manager/policy")
public class GrayPolicyController extends BaseController {

    @Autowired
    private GrayServiceManager grayServiceManager;

    @Autowired
    private GrayPolicyGroupMapper grayPolicyGroupMapper;

    @RequestMapping("/index")
    public ModelAndView index(ModelAndView model) {
        List<GrayPolicyVO> list = grayServiceManager.listGrayPolicyByDepartmentId(getDepartmentId());
        model.addObject("list", list);
        model.setViewName("gray/allPolicy");
        return model;
    }

    @RefreshGrayClient
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addPolicy(@RequestParam("policyType") String policyType, @RequestParam("policyKey") String policyKey,
                            @RequestParam("policyValue") String policyValue, @RequestParam("policyMatchType") String policyMatchType,
                            @RequestParam("policyName") String policyName, @RequestParam("remark") String remark) {
        String creator = getUdid();
        String departmentId = getDepartmentId();
        grayServiceManager.addPolicy(policyType, policyKey, policyValue, policyMatchType, policyName, remark, creator, departmentId);
        return "redirect:/gray/manager/policy/index";
    }

    @RefreshGrayClient
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String editPolicy(@RequestParam("policyType") String policyType, @RequestParam("policyKey") String policyKey,
                             @RequestParam("policyValue") String policyValue, @RequestParam("policyMatchType") String policyMatchType,
                             @RequestParam("policyName") String policyName, @RequestParam("remark") String remark,
                             @RequestParam String policyId) {
        grayServiceManager.editPolicy(policyId, policyType, policyKey, policyValue, policyMatchType, policyName, remark);
        return "redirect:/gray/manager/policy/index";
    }

    @RefreshGrayClient
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deletePolicy(@RequestParam String policyId) {
        grayServiceManager.delPolicy(policyId);
        return "redirect:/gray/manager/policy/index";
    }

    @RequestMapping("/group/index")
    public ModelAndView groupIndex(ModelAndView model) {
        List<GrayPolicyGroupVO> list = grayServiceManager.listGrayPolicyGroupByDepartmentId(getDepartmentId());
        model.addObject("list", list);
        model.setViewName("gray/policyGroup");
        return model;
    }

    @RequestMapping(value = "/group/list", method = RequestMethod.GET)
    @ResponseBody
    public RespMsg listPolicyGroup() {
        List<GrayPolicyGroupVO> list = grayServiceManager.listGrayPolicyGroupByDepartmentId(getDepartmentId());
        return RespMsg.success(list);
    }

    @RefreshGrayClient
    @RequestMapping(value = "/group/add", method = RequestMethod.POST)
    public String addGroup(@RequestParam String alias, @RequestParam Integer enable, @RequestParam String groupType, @RequestParam String remark) {
        String creator = getUdid();
        String departmentId = getDepartmentId();
        grayServiceManager.addPolicyGroup(alias, enable, groupType, remark, creator, departmentId);
        return "redirect:/gray/manager/policy/group/index";
    }

    @RefreshGrayClient
    @RequestMapping(value = "/group/edit", method = RequestMethod.POST)
    public String editGroup(@RequestParam String policyGroupId, @RequestParam String alias, @RequestParam String groupType, @RequestParam String remark) {
        grayServiceManager.editPolicyGroup(policyGroupId, alias, groupType, remark);
        return "redirect:/gray/manager/policy/group/index";
    }

    @RefreshGrayClient
    @RequestMapping(value = "/group/status", method = RequestMethod.POST)
    public String editGroup(@RequestParam String policyGroupId, @RequestParam Integer status) {
        grayServiceManager.editPolicyGroupStatus(null, null, policyGroupId, status);
        return "redirect:/gray/manager/policy/group/index";
    }

    @RefreshGrayClient
    @RequestMapping(value = "/group/delete", method = RequestMethod.POST)
    public String deleteGroup(@RequestParam String policyGroupId) {
        grayServiceManager.delPolicyGroup(policyGroupId);
        return "redirect:/gray/manager/policy/group/index";
    }

    @RequestMapping("/group/relate/index")
    public ModelAndView relateIndex(ModelAndView model, @RequestParam String policyGroupId) {
        List<GrayPolicyVO> list = grayServiceManager.listGrayPolicyByGroup(policyGroupId);
        model.addObject("list", list);
        model.addObject("policyGroupId", policyGroupId);
        GrayPolicyGroupEntity entity = grayPolicyGroupMapper.selectByPolicyGroupId(policyGroupId);
        model.addObject("creator", entity.getCreator());
        model.setViewName("gray/relatePolicy");
        return model;
    }

    @RequestMapping(value = "/group/relate/list")
    @ResponseBody
    public RespMsg relatePolicys(RedirectAttributes attr, @RequestParam String policyGroupId) {
        List<GrayPolicyVO> includs = grayServiceManager.listGrayPolicyByGroup(policyGroupId);
        List<GrayPolicyVO> all = grayServiceManager.listGrayPolicyByDepartmentId(getDepartmentId());
        List<String> policyIds = includs.stream().map(GrayPolicyVO::getPolicyId).collect(Collectors.toList());
        all = all.stream().filter(e -> !policyIds.contains(e.getPolicyId())).collect(Collectors.toList());
        attr.addAttribute("policyGroupId", policyGroupId);
        return RespMsg.success(all);
    }

    @RefreshGrayClient
    @RequestMapping(value = "/group/relate", method = RequestMethod.POST)
    public String relateGroup(RedirectAttributes attr, @RequestParam String policyGroupId, @RequestParam String policyId) {
        grayServiceManager.addPolicyGroupPolicy(policyGroupId, policyId);
        attr.addAttribute("policyGroupId", policyGroupId);
        return "redirect:/gray/manager/policy/group/relate/index";
    }

    @RefreshGrayClient
    @RequestMapping(value = "/group/unRelate", method = RequestMethod.POST)
    public String unRelateGroup(RedirectAttributes attr, @RequestParam String policyGroupId, @RequestParam String policyId) {
        grayServiceManager.delPolicyGroupPolicy(policyGroupId, policyId);
        attr.addAttribute("policyGroupId", policyGroupId);
        return "redirect:/gray/manager/policy/group/relate/index";
    }
}
