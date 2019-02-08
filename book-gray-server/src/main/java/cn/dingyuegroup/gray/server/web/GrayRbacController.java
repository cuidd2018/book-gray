package cn.dingyuegroup.gray.server.web;

import cn.dingyuegroup.gray.server.manager.RbacManager;
import cn.dingyuegroup.gray.server.model.vo.GrayRbacUserVO;
import cn.dingyuegroup.gray.server.mysql.dao.GrayRbacUserMapper;
import cn.dingyuegroup.gray.server.mysql.dao.GrayRbacUserRoleMapper;
import cn.dingyuegroup.gray.server.mysql.entity.GrayRbacUser;
import cn.dingyuegroup.gray.server.mysql.entity.GrayRbacUserRole;
import cn.dingyuegroup.gray.server.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
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
    @Autowired
    private GrayRbacUserMapper grayRbacUserMapper;
    @Autowired
    private GrayRbacUserRoleMapper grayRbacUserRoleMapper;


    @RequestMapping(value = "/user/index", method = RequestMethod.GET)
    public ModelAndView listUser(ModelAndView model) {
        String username = getUsername();
        GrayRbacUser user = grayRbacUserMapper.selectByAccount(username);
        List<GrayRbacUserVO> list = rbacManager.list(user.getDepartmentId());
        model.addObject("list", list);
        model.setViewName("user/user");
        return model;
    }

    @RequestMapping(value = "/user/add")
    public String addUser(@RequestParam String account, @RequestParam String nickname, @RequestParam String remark) {
        String username = getUsername();
        GrayRbacUser user = grayRbacUserMapper.selectByAccount(username);
        if (user == null || user.getDepartmentId() == null) {
            return "redirect:/401";
        }
        List<GrayRbacUserRole> grayRbacUserRoles = grayRbacUserRoleMapper.selectByUdid(user.getUdid());
        if (!CollectionUtils.isEmpty(grayRbacUserRoles)) {
            return "redirect:/401";
        }
        rbacManager.addUser(user.getDepartmentId(), grayRbacUserRoles.get(0).getRoleId(), nickname, remark);
        return "redirect:/gray/manager/rbac/user/index";
    }

    @RequestMapping(value = "/user/edit")
    public String editUser(@RequestParam String udid, @RequestParam String account, @RequestParam String nickname, @RequestParam String remark) {
        rbacManager.editUser(udid, account, nickname, remark);
        return "redirect:/gray/manager/rbac/user/index";
    }

    @RequestMapping(value = "/user/delete")
    public String deleteUser(@RequestParam String udid) {
        rbacManager.deleteUser(udid);
        return "redirect:/gray/manager/rbac/user/index";
    }
}
