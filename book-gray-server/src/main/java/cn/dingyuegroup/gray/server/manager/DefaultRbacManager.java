package cn.dingyuegroup.gray.server.manager;

import cn.dingyuegroup.gray.server.model.vo.*;
import cn.dingyuegroup.gray.server.mysql.dao.*;
import cn.dingyuegroup.gray.server.mysql.entity.*;
import cn.dingyuegroup.gray.server.utils.LocalStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import java.util.stream.Collectors;

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
            vo.setCreator(e.getCreator());
            if (!StringUtils.isEmpty(e.getCreator())) {
                GrayRbacUser createUser = grayRbacUserMapper.selectByUdid(e.getCreator());
                if (createUser != null) {
                    vo.setCreatorName(createUser.getNickname());
                }
            }
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
     * @param creator
     * @return
     */
    @Override
    public List<GrayRbacUserVO> listByCreator(String creator) {
        if (StringUtils.isEmpty(creator)) {
            return new ArrayList<>();
        }
        GrayRbacUser user = grayRbacUserMapper.selectByUdid(creator);
        GrayRbacDepartment department = grayRbacDepartmentMapper.selectByDepartmentId(user.getDepartmentId());
        if (department == null) {
            return new ArrayList<>();
        }
        GrayRbacUser grayRbacUser = new GrayRbacUser();
        grayRbacUser.setAccount(user.getAccount());
        grayRbacUser.setCreator(creator);
        List<GrayRbacUser> grayRbacUsers = grayRbacUserMapper.selectByCreator(grayRbacUser);
        List<GrayRbacUserVO> list = new ArrayList<>();
        grayRbacUsers.forEach(e -> {
            GrayRbacUserVO vo = new GrayRbacUserVO();
            vo.setDepartmentName(department.getDepartmentName());
            vo.setNickname(e.getNickname());
            vo.setRemark(e.getRemark());
            vo.setUdid(e.getUdid());
            vo.setAccount(e.getAccount());
            vo.setCreator(e.getCreator());
            if (!StringUtils.isEmpty(e.getCreator())) {
                GrayRbacUser createUser = grayRbacUserMapper.selectByUdid(e.getCreator());
                if (createUser != null) {
                    vo.setCreatorName(createUser.getNickname());
                }
            }
            GrayRbacUserRole grayRbacUserRole = grayRbacUserRoleMapper.selectByUdid(e.getUdid());
            if (grayRbacUserRole != null) {
                vo.setRoleId(grayRbacUserRole.getRoleId());
                GrayRbacRole grayRbacRole = grayRbacRoleMapper.selectByRoleId(grayRbacUserRole.getRoleId());
                if (grayRbacRole != null) {
                    vo.setRoleName(grayRbacRole.getRoleName());
                }
            }
            list.add(vo);
        });
        return list;
    }

    /**
     * 添加人员
     *
     * @param roleId
     * @param nickName
     * @param remark
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean addUser(String roleId, String nickName, String remark, String creator, String account) {
        if (StringUtils.isEmpty(roleId) || StringUtils.isEmpty(account)) {
            return false;
        }
        GrayRbacRole role = grayRbacRoleMapper.selectByRoleId(roleId);
        if (role == null) {
            return false;
        }
        GrayRbacDepartment department = grayRbacDepartmentMapper.selectByDepartmentId(role.getDepartmentId());
        if (department == null) {
            return false;
        }
        lock.lock();
        try {
            GrayRbacUser user = new GrayRbacUser();
            user.setUdid(GrayRbacUser.genId());
            user.setRemark(remark);
            user.setDepartmentId(role.getDepartmentId());
            user.setNickname(nickName);
            user.setCreator(creator);
            user.setAccount(account);
            user.setPassword(new BCryptPasswordEncoder().encode("123456"));
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
            grayRbacUserRole.setOldRoleId(grayRbacUserRole.getRoleId());
            grayRbacUserRole.setRoleId(roleId);
            grayRbacUserRoleMapper.updateByUdidAndRoleId(grayRbacUserRole);
        }
        return true;
    }

    /**
     * 重置密码
     *
     * @param udid
     * @param password
     * @return
     */
    @Override
    public boolean resetPassword(String udid, String password) {
        if (StringUtils.isEmpty(udid) || StringUtils.isEmpty(password)) {
            return false;
        }
        GrayRbacUser user = new GrayRbacUser();
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setUdid(udid);
        grayRbacUserMapper.updatePasswordByUdid(user);
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
     * @param account
     * @return
     */
    @Override
    public GrayUserVO userInfo(String account) {
        if (StringUtils.isEmpty(account)) {
            return null;
        }
        GrayUserVO grayUserVO = new GrayUserVO();
        grayUserVO.setAccount(account);
        GrayRbacUser user = grayRbacUserMapper.selectByAccount(account);
        if (user == null) {
            return grayUserVO;
        }
        grayUserVO.setUdid(user.getUdid());
        grayUserVO.setUsername(user.getNickname());
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
        grayUserVO.setAdmin(grayRbacRole.getIsAdmin() == 1 ? true : false);
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
        List<GrayRoleVO> list = listGrayRoleVO(grayRbacRoles);
        list.forEach(e -> {
            e.setDepartment(department.getDepartmentName());
        });
        return list;
    }

    /**
     * 获取部门管理员角色
     *
     * @return
     */
    @Override
    public List<GrayRoleVO> listRolesByCreator(String creator) {
        if (StringUtils.isEmpty(creator)) {
            return new ArrayList<>();
        }
        List<GrayRbacRole> grayRbacRoles = grayRbacRoleMapper.selectByCreator(creator);
        if (CollectionUtils.isEmpty(grayRbacRoles)) {
            return new ArrayList<>();
        }
        List<GrayRoleVO> list = listGrayRoleVO(grayRbacRoles);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        list.forEach(e -> {
            e.setDepartment("无");
            if (StringUtils.isEmpty(e.getDepartmentId())) {
                return;
            }
            GrayRbacDepartment department = grayRbacDepartmentMapper.selectByDepartmentId(e.getDepartmentId());
            if (department == null) {
                return;
            }
            e.setDepartment(department.getDepartmentName());
        });
        return list;
    }

    /**
     * 封装角色
     *
     * @param grayRbacRoles
     * @return
     */
    private List<GrayRoleVO> listGrayRoleVO(List<GrayRbacRole> grayRbacRoles) {
        List<GrayRoleVO> list = new ArrayList<>();
        grayRbacRoles.forEach(e -> {
            GrayRoleVO grayRoleVO = new GrayRoleVO();
            grayRoleVO.setRoleId(e.getRoleId());
            grayRoleVO.setRoleName(e.getRoleName());
            grayRoleVO.setDepartmentAdmin(e.getIsDepartmentAdmin() == 1 ? true : false);
            grayRoleVO.setAdmin(e.getIsAdmin() == 1 ? true : false);
            grayRoleVO.setDepartmentId(e.getDepartmentId());
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
            List<String> resourceIds = grayResourceVOS.parallelStream().map(GrayResourceVO::getResourceId).collect(Collectors.toList());
            List<String> resourceNames = grayResourceVOS.parallelStream().map(GrayResourceVO::getResourceName).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(resourceNames)) {
                resourceNames.add("无");
            }
            grayRoleVO.setResourceIds(StringUtils.collectionToDelimitedString(resourceIds, ","));
            grayRoleVO.setResourceNames(StringUtils.collectionToDelimitedString(resourceNames, ","));
            grayRoleVO.setCreator(e.getCreator());
            if (!StringUtils.isEmpty(e.getCreator())) {
                GrayRbacUser user = grayRbacUserMapper.selectByUdid(e.getCreator());
                if (user != null) {
                    grayRoleVO.setCreatorName(user.getNickname());
                }
            }
            list.add(grayRoleVO);
        });
        return list;
    }

    @Override
    public boolean addRole(String departmentId, String roleName, boolean isDepartmentAdmin, String creator) {
        GrayRbacRole role = new GrayRbacRole();
        role.setRoleName(roleName);
        role.setDepartmentId(departmentId);
        role.setRoleId(GrayRbacRole.genId());
        role.setCreator(creator);
        role.setIsDepartmentAdmin(isDepartmentAdmin ? 1 : 0);
        role.setIsAdmin(0);//不能创建总管理员
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
    public List<GrayResourceVO> listResourcesByRole(String roleId) {
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
            grayResourceVO.setEnv(grayRbacResources.getResource());
            grayResourceVOS.add(grayResourceVO);
        });
        return grayResourceVOS;
    }

    /**
     * 所有资源
     *
     * @return
     */
    @Override
    public List<GrayResourceVO> listResources() {
        List<GrayRbacResources> list = grayRbacResourcesMapper.selectAll();
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<GrayResourceVO> grayResourceVOS = new ArrayList<>();
        list.forEach(e -> {
            GrayResourceVO grayResourceVO = new GrayResourceVO();
            grayResourceVO.setResourceName(e.getResourceName());
            grayResourceVO.setResourceId(e.getResourceId());
            grayResourceVO.setEnv(e.getResource());
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
    public boolean editRoleResources(String roleId, String resourceId) {
        if (StringUtils.isEmpty(roleId)) {
            return false;
        }
        List<String> resourceIds = Arrays.asList(resourceId.split(","));
        GrayRbacRole role = grayRbacRoleMapper.selectByRoleId(roleId);
        if (role.getIsDepartmentAdmin() == 1) {//减少管理员角色的资源，需要减少下面角色的资源
            List<GrayRbacRoleResource> grayRbacRoleResources = grayRbacRoleResourceMapper.selectByRoleId(roleId);
            if (!CollectionUtils.isEmpty(grayRbacRoleResources)) {
                List<String> oldResources = grayRbacRoleResources.stream().map(GrayRbacRoleResource::getResourceId).collect(Collectors.toList());
                oldResources.removeAll(resourceIds);//减少的资源
                if (!CollectionUtils.isEmpty(oldResources)) {
                    oldResources.forEach(e -> {
                        List<GrayRbacRole> grayRbacRoles = grayRbacRoleMapper.selectByDepartmentId(role.getDepartmentId());//部门下的角色
                        if (CollectionUtils.isEmpty(grayRbacRoles)) {
                            return;
                        }
                        grayRbacRoles.forEach(f -> {
                            GrayRbacRoleResource grayRbacRoleResource = new GrayRbacRoleResource();
                            grayRbacRoleResource.setResourceId(e);
                            grayRbacRoleResource.setRoleId(f.getRoleId());
                            grayRbacRoleResourceMapper.deleteByRoleIdAndResourceId(grayRbacRoleResource);
                        });
                    });
                }
            }
        }
        grayRbacRoleResourceMapper.deleteByRoleId(roleId);
        if (StringUtils.isEmpty(resourceId)) {
            return true;
        }
        resourceIds.forEach(e -> {
            GrayRbacRoleResource grayRbacRoleResource = new GrayRbacRoleResource();
            grayRbacRoleResource.setResourceId(e);
            grayRbacRoleResource.setRoleId(roleId);
            grayRbacRoleResourceMapper.insert(grayRbacRoleResource);
        });
        return true;
    }

    /**
     * 添加资源
     *
     * @param env
     * @param resourceName
     * @return
     */
    @Override
    public boolean addResource(String env, String resourceName) {
        if (LocalStringUtils.isContainChinese(env)) {
            return false;
        }
        GrayRbacResources resources = new GrayRbacResources();
        resources.setResourceId(GrayRbacResources.genId());
        resources.setResource(env);
        resources.setResourceName(resourceName);
        grayRbacResourcesMapper.insert(resources);
        return true;
    }

    /**
     * 编辑资源
     *
     * @param resourceId
     * @param env
     * @param resourceName
     * @return
     */
    @Override
    public boolean editResource(String resourceId, String env, String resourceName) {
        if (StringUtils.isEmpty(resourceId) || StringUtils.isEmpty(resourceName)) {
            return false;
        }
        GrayRbacResources resources = new GrayRbacResources();
        resources.setResourceId(resourceId);
        resources.setResource(env);
        resources.setResourceName(resourceName);
        grayRbacResourcesMapper.updateByResourcesId(resources);
        return false;
    }

    /**
     * 删除资源
     *
     * @param resourceId
     * @return
     */
    @Override
    public boolean deleteResource(String resourceId) {
        if (StringUtils.isEmpty(resourceId)) {
            return false;
        }
        grayRbacResourcesMapper.deleteByResourcesId(resourceId);
        return true;
    }

    /**
     * 部门列表
     *
     * @return
     */
    @Override
    public List<GrayDepartmentVO> listDepartmentByCreator(String creator) {
        List<GrayRbacDepartment> list = grayRbacDepartmentMapper.selectByCreator(creator);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<GrayDepartmentVO> departmentVOS = new ArrayList<>();
        list.forEach(e -> {
            GrayDepartmentVO vo = new GrayDepartmentVO();
            vo.setDepartmentId(e.getDepartmentId());
            vo.setDepartmentName(e.getDepartmentName());
            vo.setCreator(e.getCreator());
            if (!StringUtils.isEmpty(e.getCreator())) {
                GrayRbacUser user = grayRbacUserMapper.selectByUdid(e.getCreator());
                if (user != null) {
                    vo.setCreatorName(user.getNickname());
                }
            }
            departmentVOS.add(vo);
        });
        return departmentVOS;
    }

    /**
     * 给角色设置部门
     *
     * @param roleId
     * @param departmentId
     * @return
     */
    @Override
    public boolean setRoleDepartment(String roleId, String departmentId) {
        if (StringUtils.isEmpty(roleId)) {
            return false;
        }
        GrayRbacRole role = new GrayRbacRole();
        role.setDepartmentId(null);
        role.setRoleId(roleId);
        if (!StringUtils.isEmpty(departmentId) && departmentId.contains("DEPARTMENT")) {
            GrayRbacDepartment department = grayRbacDepartmentMapper.selectByDepartmentId(departmentId);
            if (department == null) {
                return false;
            }
            role.setDepartmentId(departmentId);
        }
        grayRbacRoleMapper.editDepartmentByRoleId(role);
        return true;
    }

    /**
     * 添加部门
     *
     * @param departmentName
     * @return
     */
    @Override
    public boolean addDepartment(String departmentName, String creator) {
        if (StringUtils.isEmpty(departmentName)) {
            return false;
        }
        GrayRbacDepartment department = new GrayRbacDepartment();
        department.setDepartmentId(GrayRbacDepartment.genId());
        department.setDepartmentName(departmentName);
        department.setCreator(creator);
        grayRbacDepartmentMapper.insert(department);
        return true;
    }

    /**
     * 编辑部门
     *
     * @param departmentId
     * @param departmentName
     * @return
     */
    @Override
    public boolean editDepartment(String departmentId, String departmentName) {
        if (StringUtils.isEmpty(departmentId) || StringUtils.isEmpty(departmentName)) {
            return false;
        }
        GrayRbacDepartment department = new GrayRbacDepartment();
        department.setDepartmentId(departmentId);
        department.setDepartmentName(departmentName);
        grayRbacDepartmentMapper.updateByDepartmentId(department);
        return true;
    }

    /**
     * 删除部门
     *
     * @param departmentId
     * @return
     */
    @Override
    public boolean deleteDepartment(String departmentId) {
        if (StringUtils.isEmpty(departmentId)) {
            return false;
        }
        grayRbacDepartmentMapper.deleteByDepartmentId(departmentId);
        return true;
    }
}
