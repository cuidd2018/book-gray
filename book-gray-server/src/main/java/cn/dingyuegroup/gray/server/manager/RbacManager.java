package cn.dingyuegroup.gray.server.manager;

import cn.dingyuegroup.gray.server.model.vo.*;

import java.util.List;

/**
 * Created by 170147 on 2019/1/22.
 */
public interface RbacManager {
    /**
     * 获取部门下的人员名单
     *
     * @param departmentId
     * @return
     */
    List<GrayRbacUserVO> list(String departmentId);

    /**
     * 获取自己创建的用户
     *
     * @param username
     * @return
     */
    List<GrayRbacUserVO> listByCreator(String username);

    /**
     * 添加人员
     *
     * @param roleId
     * @param nickName
     * @param remark
     * @return
     */
    boolean addUser(String roleId, String nickName, String remark, String creator, String account);

    /**
     * 编辑用户
     *
     * @param udid
     * @param nickName
     * @param remark
     * @return
     */
    boolean editUser(String udid, String account, String nickName, String remark, String roleId);

    /**
     * 重置密码
     *
     * @param udid
     * @param password
     * @return
     */
    boolean resetPassword(String udid, String password);

    /**
     * 删除用户
     *
     * @param udid
     * @return
     */
    boolean deleteUser(String udid);

    /**
     * 获取用户所在部门信息
     *
     * @param username
     * @return
     */
    GrayUserVO userInfo(String username);

    /**
     * 获取部门下的角色信息
     *
     * @param departmentId
     * @return
     */
    List<GrayRoleVO> listRoles(String departmentId);

    /**
     * 获取部门管理员角色
     *
     * @return
     */
    List<GrayRoleVO> listRolesByCreator(String creator);

    /**
     * 添加角色
     *
     * @param departmentId
     * @param roleName
     * @param isDepartmentAdmin
     * @param creator
     * @return
     */
    boolean addRole(String departmentId, String roleName, boolean isDepartmentAdmin, String creator);

    /**
     * 编辑角色
     *
     * @param roleId
     * @param roleName
     * @return
     */
    boolean editRole(String roleId, String roleName);

    /**
     * 删除角色
     *
     * @param roleId
     * @return
     */
    boolean deleteRole(String roleId);

    /**
     * 资源列表
     *
     * @param roleId
     * @return
     */
    List<GrayResourceVO> listResourcesByRole(String roleId);

    /**
     * 所有资源
     *
     * @return
     */
    List<GrayResourceVO> listResources();

    /**
     * 更新角色资源配置
     *
     * @param roleId
     * @return
     */
    boolean editRoleResources(String roleId, String resourceId);

    /**
     * 添加资源
     *
     * @param env
     * @param resourceName
     * @return
     */
    boolean addResource(String env, String resourceName);

    /**
     * 编辑资源
     *
     * @param resourceId
     * @param env
     * @param resourceName
     * @return
     */
    boolean editResource(String resourceId, String env, String resourceName);

    /**
     * 删除资源
     *
     * @param resourceId
     * @return
     */
    boolean deleteResource(String resourceId);

    /**
     * 部门列表
     *
     * @return
     */
    List<GrayDepartmentVO> listDepartmentByCreator(String creator);

    /**
     * 给角色设置部门
     *
     * @param roleId
     * @param departmentId
     * @return
     */
    boolean setRoleDepartment(String roleId, String departmentId);

    /**
     * 添加部门
     *
     * @param departmentName
     * @return
     */
    boolean addDepartment(String departmentName, String creator);

    /**
     * 编辑部门
     *
     * @param departmentId
     * @param departmentName
     * @return
     */
    boolean editDepartment(String departmentId, String departmentName);

    /**
     * 删除部门
     *
     * @param departmentId
     * @return
     */
    boolean deleteDepartment(String departmentId);
}
