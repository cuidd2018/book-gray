package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayRbacRole;
import cn.dingyuegroup.gray.server.mysql.entity.GrayRbacUser;

import java.util.List;

public interface GrayRbacRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GrayRbacRole record);

    GrayRbacRole selectByPrimaryKey(Integer id);

    List<GrayRbacRole> selectAll();

    int updateByPrimaryKey(GrayRbacRole record);

    GrayRbacRole selectByRoleId(String roleId);

    List<GrayRbacUser> selectByDepartmentId(String departmentId);
}