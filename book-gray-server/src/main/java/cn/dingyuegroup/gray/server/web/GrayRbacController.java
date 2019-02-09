package cn.dingyuegroup.gray.server.web;

import cn.dingyuegroup.gray.server.manager.RbacManager;
import cn.dingyuegroup.gray.server.model.resp.ErrorCode;
import cn.dingyuegroup.gray.server.model.resp.RespMsg;
import cn.dingyuegroup.gray.server.model.vo.GrayRbacUserVO;
import cn.dingyuegroup.gray.server.model.vo.GrayResourceVO;
import cn.dingyuegroup.gray.server.model.vo.GrayRoleVO;
import cn.dingyuegroup.gray.server.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by 170147 on 2019/1/22.
 */
@Controller
@RequestMapping("/gray/manager/rbac")
public class GrayRbacController extends BaseController {
    @Autowired
    private RbacManager rbacManager;


    @RequestMapping(value = "/user/index", method = RequestMethod.GET)
    public ModelAndView listUser(ModelAndView model) {
        String username = getUsername();
        List<GrayRbacUserVO> list = rbacManager.listByCreator(username);
        model.addObject("list", list);
        model.setViewName("user/user");
        return model;
    }

    @RequestMapping(value = "/user/add")
    public String addUser(@RequestParam String account, @RequestParam String nickname, @RequestParam String remark, @RequestParam String roleId) {
        String username = getUsername();
        String departmentId = getDepartmentId();
        if (StringUtils.isEmpty(departmentId)) {
            return "redirect:/401";
        }
        rbacManager.addUser(departmentId, roleId, nickname, remark, username, account);
        return "redirect:/gray/manager/rbac/user/index";
    }

    @RequestMapping(value = "/user/edit")
    public String editUser(@RequestParam String udid, @RequestParam String account, @RequestParam String nickname, @RequestParam String remark, @RequestParam String roleId) {
        rbacManager.editUser(udid, account, nickname, remark, roleId);
        return "redirect:/gray/manager/rbac/user/index";
    }

    @RequestMapping(value = "/user/delete")
    public String deleteUser(@RequestParam String udid) {
        rbacManager.deleteUser(udid);
        return "redirect:/gray/manager/rbac/user/index";
    }

    @RequestMapping(value = "/role/list")
    @ResponseBody
    public RespMsg listRoles() {
        String departmentId = getDepartmentId();
        if (StringUtils.isEmpty(departmentId)) {
            return RespMsg.error(ErrorCode.SYSTEM_ERROR, "用户信息不完善，缺少部门信息！");
        }
        List<GrayRoleVO> list = rbacManager.listRoles(departmentId);
        return RespMsg.success(list);
    }

    @RequestMapping(value = "/role/index")
    public ModelAndView roleIndex(ModelAndView model) {
        String departmentId = getDepartmentId();
        List<GrayRoleVO> list = rbacManager.listRoles(departmentId);
        model.addObject("list", list);
        model.setViewName("role/role");
        return model;
    }

    @RequestMapping(value = "/role/add")
    public String addRole(@RequestParam String roleName) {
        String departmentId = getDepartmentId();
        String username = getUsername();
        rbacManager.addRole(departmentId, roleName, 0, username);
        return "redirect:/gray/manager/rbac/role/index";
    }

    @RequestMapping(value = "/role/edit")
    public String editRole(@RequestParam String roleId, @RequestParam String roleName) {
        rbacManager.editRole(roleId, roleName);
        return "redirect:/gray/manager/rbac/role/index";
    }

    @RequestMapping(value = "/role/delete")
    public String deleteRole(@RequestParam String roleId) {
        rbacManager.deleteRole(roleId);
        return "redirect:/gray/manager/rbac/role/index";
    }

    @RequestMapping(value = "/resources")
    @ResponseBody
    public RespMsg listResources() {
        String roleId = getRoleId();
        if (StringUtils.isEmpty(roleId)) {
            return RespMsg.error(ErrorCode.SYSTEM_ERROR, "用户信息不完善，缺少角色信息！");
        }
        List<GrayResourceVO> list = rbacManager.listResources(roleId);
        return RespMsg.success(list);
    }

    @RequestMapping(value = "/resources/edit")
    public String editResources(@RequestParam String roleId, @RequestParam String resourceId) {
        rbacManager.editResources(roleId, resourceId);
        return "redirect:/gray/manager/rbac/role/index";
    }

}
