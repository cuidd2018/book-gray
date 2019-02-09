package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayRbacRoleResource;

import java.util.List;

public interface GrayRbacRoleResourceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GrayRbacRoleResource record);

    GrayRbacRoleResource selectByPrimaryKey(Integer id);

    List<GrayRbacRoleResource> selectAll();

    int updateByPrimaryKey(GrayRbacRoleResource record);

    GrayRbacRoleResource selectByResourceId(String resourceId);

    List<GrayRbacRoleResource> selectByRoleId(String roleId);

    int deleteByRoleId(String roleId);
}