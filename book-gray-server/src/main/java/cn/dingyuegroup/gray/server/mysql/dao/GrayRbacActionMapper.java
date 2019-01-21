package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayRbacAction;

import java.util.List;

public interface GrayRbacActionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GrayRbacAction record);

    GrayRbacAction selectByPrimaryKey(Integer id);

    List<GrayRbacAction> selectAll();

    int updateByPrimaryKey(GrayRbacAction record);
}