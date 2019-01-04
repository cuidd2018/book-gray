package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayInstanceEntity;

import java.util.List;

public interface GrayInstanceMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(GrayInstanceEntity record);

    GrayInstanceEntity selectByPrimaryKey(Integer id);

    List<GrayInstanceEntity> selectAll();

    int updateByPrimaryKey(GrayInstanceEntity record);

    GrayInstanceEntity selectByInstanceId(String instanceId);

    List<GrayInstanceEntity> selectByServiceId(String serviceId);

    int deleteByInstanceId(String instanceId);

    int updateByInstanceId(GrayInstanceEntity record);
}