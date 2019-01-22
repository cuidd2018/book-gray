package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayRbacResources;

import java.util.List;

public interface GrayRbacResourcesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GrayRbacResources record);

    GrayRbacResources selectByPrimaryKey(Integer id);

    List<GrayRbacResources> selectAll();

    int updateByPrimaryKey(GrayRbacResources record);

    GrayRbacResources selectByResourceId(String resourceId);
}