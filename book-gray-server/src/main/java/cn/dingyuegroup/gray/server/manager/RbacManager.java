package cn.dingyuegroup.gray.server.manager;

import cn.dingyuegroup.gray.server.model.vo.GrayRbacUserVO;
import cn.dingyuegroup.gray.server.model.vo.GrayRoleVO;
import cn.dingyuegroup.gray.server.model.vo.GrayUserVO;

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
     * @param departmentId
     * @param roleId
     * @param nickName
     * @param remark
     * @return
     */
    boolean addUser(String departmentId, String roleId, String nickName, String remark, String creator, String account);

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
     * 添加角色
     *
     * @param departmentId
     * @param roleName
     * @param isDepartmentAdmin
     * @param creator
     * @return
     */
    boolean addRole(String departmentId, String roleName, Integer isDepartmentAdmin, String creator);

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
     * @param roleId
     * @return
     */
    boolean deleteRole(String roleId);
}
