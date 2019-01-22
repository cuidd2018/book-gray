package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayRbacUserRole;

import java.util.List;

public interface GrayRbacUserRoleMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(GrayRbacUserRole record);

    GrayRbacUserRole selectByPrimaryKey(Integer id);

    List<GrayRbacUserRole> selectAll();

    int updateByPrimaryKey(GrayRbacUserRole record);

    List<GrayRbacUserRole> selectByUdid(String udid);

    List<GrayRbacUserRole> selectByRoleId(String roleId);

    int updateByUdidAndRoleId(GrayRbacUserRole record);
}