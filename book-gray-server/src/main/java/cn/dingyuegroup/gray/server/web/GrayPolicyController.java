package cn.dingyuegroup.gray.server.web;

import cn.dingyuegroup.gray.server.manager.GrayServiceManager;
import cn.dingyuegroup.gray.server.model.resp.RespMsg;
import cn.dingyuegroup.gray.server.model.vo.GrayPolicyGroupVO;
import cn.dingyuegroup.gray.server.mysql.entity.GrayPolicyEntity;
import cn.dingyuegroup.gray.server.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by 170147 on 2019/1/8.
 */
@Controller
@RequestMapping("/gray/manager/policy")
public class GrayPolicyController extends BaseController {

    @Autowired
    private GrayServiceManager grayServiceManager;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Void> addPolicy(@RequestParam("policyType") String policyType, @RequestParam("policyKey") String policyKey
            , @RequestParam("policyValue") String policyValue, @RequestParam("policyMatchType") String policyMatchType) {
        grayServiceManager.addPolicy(policyType, policyKey, policyValue, policyMatchType);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Void> editPolicy(@RequestParam("policyType") String policyType, @RequestParam("policyKey") String policyKey
            , @RequestParam("policyValue") String policyValue, @RequestParam("policyMatchType") String policyMatchType, @RequestParam String policyId) {
        grayServiceManager.editPolicy(policyId, policyType, policyKey, policyValue, policyMatchType);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Void> deletePolicy(@RequestParam String policyId) {
        grayServiceManager.delPolicy(policyId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<GrayPolicyEntity>> listPolicy(@RequestParam String groupId) {
        List<GrayPolicyEntity> list = grayServiceManager.listGrayPolicyByGroup(groupId);
        return ResponseEntity.ok(list);
    }

    @RequestMapping("/group/index")
    public ModelAndView index(ModelAndView model) {
        List<GrayPolicyGroupVO> list = grayServiceManager.listAllGrayPolicyGroup();
        model.addObject("list", list);
        model.setViewName("gray/policyGroup");
        return model;
    }

    @RequestMapping(value = "/group/list", method = RequestMethod.GET)
    @ResponseBody
    public RespMsg listPolicyGroup() {
        List<GrayPolicyGroupVO> list = grayServiceManager.listAllGrayPolicyGroup();
        return RespMsg.success(list);
    }

    @RequestMapping(value = "/group/add")
    public String addGroup(@RequestParam String alias, @RequestParam Integer enable, @RequestParam String groupType, @RequestParam String remark) {
        grayServiceManager.addPolicyGroup(alias, enable, groupType, remark);
        return "redirect:/gray/manager/policy/group/index";
    }

    @RequestMapping(value = "/group/edit")
    public String editGroup(@RequestParam String policyGroupId, @RequestParam String alias, @RequestParam String groupType, @RequestParam String remark) {
        grayServiceManager.editPolicyGroup(policyGroupId, alias, groupType, remark);
        return "redirect:/gray/manager/policy/group/index";
    }

    @RequestMapping(value = "/group/status")
    public String editGroup(@RequestParam String policyGroupId, @RequestParam Integer status) {
        grayServiceManager.editPolicyGroupStatus(null, null, policyGroupId, status);
        return "redirect:/gray/manager/policy/group/index";
    }

    @RequestMapping(value = "/group/delete")
    public String deleteGroup(@RequestParam String policyGroupId) {
        grayServiceManager.delPolicyGroup(policyGroupId);
        return "redirect:/gray/manager/policy/group/index";
    }

    @RequestMapping(value = "/group/relate", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Void> relateGroup(@RequestParam String groupId, @RequestParam String policyId) {
        grayServiceManager.addPolicyGroupPolicy(groupId, policyId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/group/unRelate", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Void> unRelateGroup(@RequestParam String groupId, @RequestParam String policyId) {
        grayServiceManager.delPolicyGroupPolicy(groupId, policyId);
        return ResponseEntity.ok().build();
    }
}
