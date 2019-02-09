package cn.dingyuegroup.gray.server.manager;

import cn.dingyuegroup.gray.server.model.vo.GrayRbacUserVO;
import cn.dingyuegroup.gray.server.model.vo.GrayResourceVO;
import cn.dingyuegroup.gray.server.model.vo.GrayRoleVO;
import cn.dingyuegroup.gray.server.model.vo.GrayUserVO;
import cn.dingyuegroup.gray.server.mysql.dao.*;
import cn.dingyuegroup.gray.server.mysql.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by 170147 on 2019/1/22.
 */
@Service
public class DefaultRbacManager implements RbacManager {

    private Lock lock = new ReentrantLock();

    @Autowired
    private GrayRbacDepartmentMapper grayRbacDepartmentMapper;

    @Autowired
    private GrayRbacUserMapper grayRbacUserMapper;

    @Autowired
    private GrayRbacRoleMapper grayRbacRoleMapper;

    @Autowired
    private GrayRbacUserRoleMapper grayRbacUserRoleMapper;

    @Autowired
    private GrayRbacRoleResourceMapper grayRbacRoleResourceMapper;

    @Autowired
    private GrayRbacResourcesMapper grayRbacResourcesMapper;

    /**
     * 获取部门下的人员名单
     *
     * @param departmentId
     * @return
     */
    @Override
    public List<GrayRbacUserVO> list(String departmentId) {
        List<GrayRbacUserVO> list = new ArrayList<>();
        if (StringUtils.isEmpty(departmentId)) {
            return list;
        }
        GrayRbacDepartment department = grayRbacDepartmentMapper.selectByDepartmentId(departmentId);
        if (department == null) {
            return list;
        }
        List<GrayRbacUser> grayRbacUsers = grayRbacUserMapper.selectByDepartmentId(departmentId);
        if (CollectionUtils.isEmpty(grayRbacUsers)) {
            return list;
        }
        grayRbacUsers.forEach(e -> {
            GrayRbacUserVO vo = new GrayRbacUserVO();
            vo.setDepartmentName(department.getDepartmentName());
            vo.setNickname(e.getNickname());
            vo.setRemark(e.getRemark());
            vo.setUdid(e.getUdid());
            vo.setAccount(e.getAccount());
            GrayRbacUserRole grayRbacUserRole = grayRbacUserRoleMapper.selectByUdid(e.getUdid());
            if (grayRbacUserRole == null) {
                return;
            }
            GrayRbacRole grayRbacRole = grayRbacRoleMapper.selectByRoleId(grayRbacUserRole.getRoleId());
            if (grayRbacRole != null) {
                vo.setRoleName(grayRbacRole.getRoleName());
            }
            list.add(vo);
        });
        return list;
    }

    /**
     * 获取自己创建的用户
     *
     * @param username
     * @return
     */
    @Override
    public List<GrayRbacUserVO> listByCreator(String username) {
        if (StringUtils.isEmpty(username)) {
            return new ArrayList<>();
        }
        GrayRbacUser user = grayRbacUserMapper.selectByAccount(username);
        GrayRbacDepartment department = grayRbacDepartmentMapper.selectByDepartmentId(user.getDepartmentId());
        if (department == null) {
            return new ArrayList<>();
        }
        GrayRbacUser grayRbacUser = new GrayRbacUser();
        grayRbacUser.setAccount(username);
        grayRbacUser.setCreator(username);
        List<GrayRbacUser> grayRbacUsers = grayRbacUserMapper.selectByCreator(grayRbacUser);
        List<GrayRbacUserVO> list = new ArrayList<>();
        grayRbacUsers.forEach(e -> {
            GrayRbacUserVO vo = new GrayRbacUserVO();
            vo.setDepartmentName(department.getDepartmentName());
            vo.setNickname(e.getNickname());
            vo.setRemark(e.getRemark());
            vo.setUdid(e.getUdid());
            vo.setAccount(e.getAccount());
            GrayRbacUserRole grayRbacUserRole = grayRbacUserRoleMapper.selectByUdid(e.getUdid());
            if (grayRbacUserRole == null) {
                return;
            }
            vo.setRoleId(grayRbacUserRole.getRoleId());
            GrayRbacRole grayRbacRole = grayRbacRoleMapper.selectByRoleId(grayRbacUserRole.getRoleId());
            if (grayRbacRole != null) {
                vo.setRoleName(grayRbacRole.getRoleName());
            }
            list.add(vo);
        });
        return list;
    }

    /**
     * 添加人员
     *
     * @param departmentId
     * @param roleId
     * @param nickName
     * @param remark
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean addUser(String departmentId, String roleId, String nickName, String remark, String creator, String account) {
        GrayRbacDepartment department = grayRbacDepartmentMapper.selectByDepartmentId(departmentId);
        if (department == null) {
            return false;
        }
        GrayRbacRole role = grayRbacRoleMapper.selectByRoleId(roleId);
        if (role == null) {
            return false;
        }
        lock.lock();
        try {
            GrayRbacUser user = new GrayRbacUser();
            user.setUdid(GrayRbacUser.genId());
            user.setRemark(remark);
            user.setDepartmentId(departmentId);
            user.setNickname(nickName);
            user.setCreator(creator);
            user.setAccount(account);
            grayRbacUserMapper.insert(user);
            GrayRbacUserRole grayRbacUserRole = new GrayRbacUserRole();
            grayRbacUserRole.setUdid(user.getUdid());
            grayRbacUserRole.setRoleId(roleId);
            grayRbacUserRoleMapper.insert(grayRbacUserRole);
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            lock.unlock();
        }
        return true;
    }

    /**
     * 编辑用户
     *
     * @param udid
     * @param nickName
     * @param remark
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean editUser(String udid, String account, String nickName, String remark, String roleId) {
        GrayRbacUser user = grayRbacUserMapper.selectByUdid(udid);
        if (user == null) {
            return false;
        }
        user.setRemark(remark);
        user.setNickname(nickName);
        user.setAccount(account);
        grayRbacUserMapper.updateByUdid(user);
        GrayRbacUserRole grayRbacUserRole = grayRbacUserRoleMapper.selectByUdid(udid);
        if (grayRbacUserRole == null) {
            grayRbacUserRole = new GrayRbacUserRole();
            grayRbacUserRole.setUdid(user.getUdid());
            grayRbacUserRole.setRoleId(roleId);
            grayRbacUserRoleMapper.insert(grayRbacUserRole);
        } else {
            grayRbacUserRole.setRoleId(roleId);
            grayRbacUserRole.setOldRoleId(grayRbacUserRole.getRoleId());
            grayRbacUserRoleMapper.updateByUdidAndRoleId(grayRbacUserRole);
        }
        return true;
    }

    /**
     * 删除用户
     *
     * @param udid
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean deleteUser(String udid) {
        grayRbacUserMapper.deleteByUdid(udid);
        grayRbacUserRoleMapper.deleteByUdid(udid);
        return true;
    }

    /**
     * 获取用户所在部门信息
     *
     * @param username
     * @return
     */
    @Override
    public GrayUserVO userInfo(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        GrayUserVO grayUserVO = new GrayUserVO();
        grayUserVO.setUsername(username);
        GrayRbacUser user = grayRbacUserMapper.selectByAccount(username);
        if (user == null) {
            return grayUserVO;
        }
        if (!StringUtils.isEmpty(user.getDepartmentId())) {
            GrayRbacDepartment department = grayRbacDepartmentMapper.selectByDepartmentId(user.getDepartmentId());
            if (department != null) {
                grayUserVO.setDepartment(department.getDepartmentName());
            }
        }
        GrayRbacUserRole grayRbacUserRole = grayRbacUserRoleMapper.selectByUdid(user.getUdid());
        if (grayRbacUserRole == null || StringUtils.isEmpty(grayRbacUserRole.getRoleId())) {
            return grayUserVO;
        }
        GrayRbacRole grayRbacRole = grayRbacRoleMapper.selectByRoleId(grayRbacUserRole.getRoleId());
        if (grayRbacRole == null) {
            return grayUserVO;
        }
        grayUserVO.setDepartmentAdmin(grayRbacRole.getIsDepartmentAdmin() == 1 ? true : false);
        grayUserVO.setRoles(grayRbacRole.getRoleName());
        return grayUserVO;
    }

    /**
     * 获取部门下的角色信息
     *
     * @param departmentId
     * @return
     */
    @Override
    public List<GrayRoleVO> listRoles(String departmentId) {
        if (StringUtils.isEmpty(departmentId)) {
            return new ArrayList<>();
        }
        GrayRbacDepartment department = grayRbacDepartmentMapper.selectByDepartmentId(departmentId);
        if (department == null) {
            return new ArrayList<>();
        }
        List<GrayRbacRole> grayRbacRoles = grayRbacRoleMapper.selectByDepartmentId(departmentId);
        if (CollectionUtils.isEmpty(grayRbacRoles)) {
            return new ArrayList<>();
        }
        List<GrayRoleVO> list = new ArrayList<>();
        grayRbacRoles.forEach(e -> {
            GrayRoleVO grayRoleVO = new GrayRoleVO();
            grayRoleVO.setRoleId(e.getRoleId());
            grayRoleVO.setRoleName(e.getRoleName());
            grayRoleVO.setDepartmentAdmin(e.getIsDepartmentAdmin() == 1 ? true : false);
            grayRoleVO.setDepartment(department.getDepartmentName());
            List<GrayRbacRoleResource> grayRbacRoleResources = grayRbacRoleResourceMapper.selectByRoleId(e.getRoleId());
            List<GrayResourceVO> grayResourceVOS = new ArrayList<>();
            if (!CollectionUtils.isEmpty(grayRbacRoleResources)) {
                grayRbacRoleResources.forEach(f -> {
                            GrayRbacResources grayRbacResources = grayRbacResourcesMapper.selectByResourceId(f.getResourceId());
                            if (grayRbacResources == null) {
                                return;
                            }
                            GrayResourceVO grayResourceVO = new GrayResourceVO();
                            grayResourceVO.setResourceId(grayRbacResources.getResourceId());
                            grayResourceVO.setResourceName(grayRbacResources.getResourceName());
                            grayResourceVOS.add(grayResourceVO);
                        }
                );
            }
            grayRoleVO.setResourceS(grayResourceVOS);
            list.add(grayRoleVO);
        });
        return list;
    }

    @Override
    public boolean addRole(String departmentId, String roleName, Integer isDepartmentAdmin, String creator) {
        GrayRbacRole role = new GrayRbacRole();
        role.setRoleName(roleName);
        role.setDepartmentId(departmentId);
        role.setRoleId(GrayRbacRole.genId());
        role.setCreator(creator);
        role.setIsDepartmentAdmin(isDepartmentAdmin == null ? 0 : isDepartmentAdmin);
        grayRbacRoleMapper.insert(role);
        return true;
    }

    /**
     * 编辑角色
     *
     * @param roleId
     * @param roleName
     * @return
     */
    @Override
    public boolean editRole(String roleId, String roleName) {
        GrayRbacRole role = new GrayRbacRole();
        role.setRoleId(roleId);
        role.setRoleName(roleName);
        grayRbacRoleMapper.editByRoleId(role);
        return true;
    }

    /**
     * 删除角色
     *
     * @param roleId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean deleteRole(String roleId) {
        grayRbacRoleMapper.deleteByRoleId(roleId);
        grayRbacUserRoleMapper.deleteByRoleId(roleId);
        grayRbacRoleResourceMapper.deleteByRoleId(roleId);
        return true;
    }

    @Override
    public List<GrayResourceVO> listResources(String roleId) {
        List<GrayRbacRoleResource> list = grayRbacRoleResourceMapper.selectByRoleId(roleId);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<GrayResourceVO> grayResourceVOS = new ArrayList<>();
        list.forEach(e -> {
            GrayRbacResources grayRbacResources = grayRbacResourcesMapper.selectByResourceId(e.getResourceId());
            if (grayRbacResources == null) {
                return;
            }
            GrayResourceVO grayResourceVO = new GrayResourceVO();
            grayResourceVO.setResourceName(grayRbacResources.getResourceName());
            grayResourceVO.setResourceId(grayRbacResources.getResourceId());
            grayResourceVOS.add(grayResourceVO);
        });
        return grayResourceVOS;
    }

    /**
     * 更新角色资源配置
     *
     * @param roleId
     * @return
     */
    @Override
    public boolean editResources(String roleId, String resourceId) {
        if (StringUtils.isEmpty(roleId)) {
            return false;
        }
        grayRbacRoleResourceMapper.deleteByRoleId(roleId);
        if (StringUtils.isEmpty(resourceId)) {
            return true;
        }
        List<String> resourceIds = Arrays.asList(resourceId.split(","));
        resourceIds.forEach(e -> {
            GrayRbacRoleResource grayRbacRoleResource = new GrayRbacRoleResource();
            grayRbacRoleResource.setResourceId(e);
            grayRbacRoleResource.setRoleId(roleId);
            grayRbacRoleResourceMapper.insert(grayRbacRoleResource);
        });
        return true;
    }
}
