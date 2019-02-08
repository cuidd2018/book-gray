package cn.dingyuegroup.gray.server.web;

import cn.dingyuegroup.gray.server.manager.RbacManager;
import cn.dingyuegroup.gray.server.model.vo.GrayRbacUserVO;
import cn.dingyuegroup.gray.server.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
        rbacManager.addUser(departmentId, roleId, nickname, remark, username);
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
}
