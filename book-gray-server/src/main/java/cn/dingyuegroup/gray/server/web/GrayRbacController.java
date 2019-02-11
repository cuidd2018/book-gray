package cn.dingyuegroup.gray.server.web;

import cn.dingyuegroup.gray.server.manager.RbacManager;
import cn.dingyuegroup.gray.server.model.resp.RespMsg;
import cn.dingyuegroup.gray.server.model.vo.GrayDepartmentVO;
import cn.dingyuegroup.gray.server.model.vo.GrayRbacUserVO;
import cn.dingyuegroup.gray.server.model.vo.GrayResourceVO;
import cn.dingyuegroup.gray.server.model.vo.GrayRoleVO;
import cn.dingyuegroup.gray.server.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
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
        String creator = getUdid();
        List<GrayRbacUserVO> list = rbacManager.listByCreator(creator);
        model.addObject("list", list);
        model.setViewName("user/user");
        return model;
    }

    @RequestMapping(value = "/user/add")
    public String addUser(@RequestParam String account, @RequestParam String nickname, @RequestParam String remark, @RequestParam String roleId) {
        String udid = getUdid();
        rbacManager.addUser(roleId, nickname, remark, udid, account);
        return "redirect:/gray/manager/rbac/user/index";
    }

    @RequestMapping(value = "/user/edit")
    public String editUser(@RequestParam String udid, @RequestParam String account, @RequestParam String nickname, @RequestParam String remark, @RequestParam String roleId) {
        rbacManager.editUser(udid, account, nickname, remark, roleId);
        return "redirect:/gray/manager/rbac/user/index";
    }

    @RequestMapping(value = "/user/password")
    public String resetPassword(@RequestParam String udid, @RequestParam String password) {
        rbacManager.resetPassword(udid, password);
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
        String creator = getUdid();
        List<GrayRoleVO> list = rbacManager.listRolesByCreator(creator);
        return RespMsg.success(list);
    }

    @RequestMapping(value = "/role/index")
    public ModelAndView roleIndex(ModelAndView model) {
        String creator = getUdid();
        List<GrayRoleVO> list = rbacManager.listRolesByCreator(creator);
        model.addObject("list", list);
        model.setViewName("role/role");
        return model;
    }

    @RequestMapping(value = "/role/add")
    public String addRole(@RequestParam String roleName) {
        String departmentId = getDepartmentId();
        String creator = getUdid();
        boolean isAdmin = isAdmin();
        boolean isDepartmentAdmin = false;
        if (isAdmin) {//只有总管理员才可以创建部门管理员
            isDepartmentAdmin = true;
            departmentId = null;
        }
        rbacManager.addRole(departmentId, roleName, isDepartmentAdmin, creator);
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
        List<GrayResourceVO> list = new ArrayList<>();
        boolean isDepartmentAdmin = isDepartmentAdmin();
        boolean isAdmin = isAdmin();
        if (isAdmin) {//总管理员
            list = rbacManager.listResources();
        } else if (isDepartmentAdmin) {//部门管理员
            String roleId = getRoleId();
            list = rbacManager.listResourcesByRole(roleId);
        }
        return RespMsg.success(list);
    }

    @RequestMapping(value = "/resources/index", method = RequestMethod.GET)
    public ModelAndView resourcesIndex(ModelAndView model) {
        List<GrayResourceVO> list = rbacManager.listResources();
        model.addObject("list", list);
        model.setViewName("resources/resources");
        return model;
    }

    @RequestMapping(value = "/resources/add")
    public String addResources(@RequestParam String env, @RequestParam String resourceName) {
        rbacManager.addResource(env, resourceName);
        return "redirect:/gray/manager/rbac/resources/index";
    }

    @RequestMapping(value = "/resources/edit")
    public String editResources(@RequestParam String resourcesId, @RequestParam String env, @RequestParam String resourceName) {
        rbacManager.editResource(resourcesId, env, resourceName);
        return "redirect:/gray/manager/rbac/resources/index";
    }

    @RequestMapping(value = "/resources/delete")
    public String deleteResources(@RequestParam String resourcesId) {
        rbacManager.deleteResource(resourcesId);
        return "redirect:/gray/manager/rbac/resources/index";
    }

    @RequestMapping(value = "/role/resources/edit")
    public String editRoleResources(@RequestParam String roleId, @RequestParam String resourceId) {
        rbacManager.editRoleResources(roleId, resourceId);
        return "redirect:/gray/manager/rbac/role/index";
    }

    @RequestMapping(value = "/department/index", method = RequestMethod.GET)
    public ModelAndView listDepartment(ModelAndView model) {
        String creator = getUdid();
        List<GrayDepartmentVO> list = rbacManager.listDepartmentByCreator(creator);
        model.addObject("list", list);
        model.setViewName("department/department");
        return model;
    }

    @RequestMapping(value = "/department/list")
    @ResponseBody
    public RespMsg listDepartments() {
        String creator = getUdid();
        List<GrayDepartmentVO> list = rbacManager.listDepartmentByCreator(creator);
        return RespMsg.success(list);
    }

    @RequestMapping(value = "/role/department")
    public String roleDepartment(@RequestParam String roleId, @RequestParam String departmentId) {
        rbacManager.setRoleDepartment(roleId, departmentId);
        return "redirect:/gray/manager/rbac/role/index";
    }

    @RequestMapping(value = "/department/add")
    public String addDepartment(@RequestParam String departmentName) {
        String creator = getUdid();
        rbacManager.addDepartment(departmentName, creator);
        return "redirect:/gray/manager/rbac/department/index";
    }

    @RequestMapping(value = "/department/edit")
    public String editDepartment(@RequestParam String departmentId, @RequestParam String departmentName) {
        rbacManager.editDepartment(departmentId, departmentName);
        return "redirect:/gray/manager/rbac/department/index";
    }

    @RequestMapping(value = "/department/delete")
    public String deleteDepartment(@RequestParam String departmentId) {
        rbacManager.deleteDepartment(departmentId);
        return "redirect:/gray/manager/rbac/department/index";
    }
}
