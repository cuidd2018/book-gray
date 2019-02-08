package cn.dingyuegroup.gray.server.manager;

import cn.dingyuegroup.gray.server.model.vo.GrayRbacUserVO;
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
     * 添加人员
     *
     * @param departmentId
     * @param roleId
     * @param nickName
     * @param remark
     * @return
     */
    boolean addUser(String departmentId, String roleId, String nickName, String remark);

    /**
     * 编辑用户
     *
     * @param udid
     * @param nickName
     * @param remark
     * @return
     */
    boolean editUser(String udid, String account, String nickName, String remark);

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
    GrayUserVO getDepartment(String username);
}
