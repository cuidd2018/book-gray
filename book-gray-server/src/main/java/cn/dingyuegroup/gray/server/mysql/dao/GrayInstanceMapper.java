package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayInstance;

import java.util.List;

public interface GrayInstanceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GrayInstance record);

    GrayInstance selectByPrimaryKey(Integer id);

    List<GrayInstance> selectAll();

    int updateByPrimaryKey(GrayInstance record);
}