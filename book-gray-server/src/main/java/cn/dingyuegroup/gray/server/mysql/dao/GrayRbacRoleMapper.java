package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayRbacRole;

import java.util.List;

public interface GrayRbacRoleMapper {
    int deleteByRoleId(String id);

    int insert(GrayRbacRole record);

    GrayRbacRole selectByPrimaryKey(Integer id);

    List<GrayRbacRole> selectAll();

    int updateByPrimaryKey(GrayRbacRole record);

    GrayRbacRole selectByRoleId(String roleId);

    List<GrayRbacRole> selectByDepartmentId(String departmentId);

    List<GrayRbacRole> selectByCreator(String creator);

    int editByRoleId(GrayRbacRole record);

    int editDepartmentByRoleId(GrayRbacRole record);
}