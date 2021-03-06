package cn.dingyuegroup.gray.server.web.base;

import cn.dingyuegroup.gray.server.mysql.dao.*;
import cn.dingyuegroup.gray.server.mysql.entity.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 170147 on 2019/2/1.
 */
public abstract class BaseController {

    private Logger logger = Logger.getLogger(getClass());

    @Autowired
    private GrayRbacUserRoleMapper grayRbacUserRoleMapper;
    @Autowired
    private GrayRbacUserMapper grayRbacUserMapper;
    @Autowired
    private GrayRbacRoleResourceMapper grayRbacRoleResourceMapper;
    @Autowired
    private GrayRbacResourcesMapper grayRbacResourcesMapper;
    @Autowired
    private GrayRbacRoleMapper grayRbacRoleMapper;

    /**
     * 取登录用户名
     *
     * @return
     */
    public String getUsername() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userDetails != null) {
                return userDetails.getUsername();
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }

    public String getUdid() {
        String username = getUsername();
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        GrayRbacUser grayRbacUser = grayRbacUserMapper.selectByAccount(username);
        if (grayRbacUser == null) {
            return null;
        }
        return grayRbacUser.getUdid();
    }

    public String getDepartmentId() {
        String username = getUsername();
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        GrayRbacUser grayRbacUser = grayRbacUserMapper.selectByAccount(username);
        if (grayRbacUser == null) {
            return null;
        }
        return grayRbacUser.getDepartmentId();
    }

    public String getRoleId() {
        String username = getUsername();
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        String udid = getUdid();
        if (StringUtils.isEmpty(udid)) {
            return null;
        }
        GrayRbacUserRole grayRbacUserRole = grayRbacUserRoleMapper.selectByUdid(udid);
        if (grayRbacUserRole == null) {
            return null;
        }
        return grayRbacUserRole.getRoleId();
    }

    public boolean isDepartmentAdmin() {
        String username = getUsername();
        if (StringUtils.isEmpty(username)) {
            return false;
        }
        String udid = getUdid();
        if (StringUtils.isEmpty(udid)) {
            return false;
        }
        GrayRbacUserRole grayRbacUserRole = grayRbacUserRoleMapper.selectByUdid(udid);
        if (grayRbacUserRole == null) {
            return false;
        }
        GrayRbacRole role = grayRbacRoleMapper.selectByRoleId(grayRbacUserRole.getRoleId());
        if (role == null) {
            return false;
        }
        return role.getIsDepartmentAdmin() == 1 ? true : false;
    }

    public boolean isAdmin() {
        String username = getUsername();
        if (StringUtils.isEmpty(username)) {
            return false;
        }
        String udid = getUdid();
        if (StringUtils.isEmpty(udid)) {
            return false;
        }
        GrayRbacUserRole grayRbacUserRole = grayRbacUserRoleMapper.selectByUdid(udid);
        if (grayRbacUserRole == null) {
            return false;
        }
        GrayRbacRole role = grayRbacRoleMapper.selectByRoleId(grayRbacUserRole.getRoleId());
        if (role == null) {
            return false;
        }
        return role.getIsAdmin() == 1 ? true : false;
    }

    public List<GrayRbacResources> getResources() {
        String roleId = getRoleId();
        if (StringUtils.isEmpty(roleId)) {
            return new ArrayList<>();
        }
        List<GrayRbacRoleResource> list = grayRbacRoleResourceMapper.selectByRoleId(roleId);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<GrayRbacResources> grayRbacResources = new ArrayList<>();
        list.forEach(e -> {
            GrayRbacResources resources = grayRbacResourcesMapper.selectByResourceId(e.getResourceId());
            if (resources == null) {
                return;
            }
            grayRbacResources.add(resources);
        });
        return grayRbacResources;
    }
}
