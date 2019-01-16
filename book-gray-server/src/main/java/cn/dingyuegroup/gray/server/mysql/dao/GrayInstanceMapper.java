package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayInstanceEntity;
import cn.dingyuegroup.gray.server.mysql.entity.GrayPolicyGroupEntity;

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

    int updateGrayStatusByInstanceId(GrayInstanceEntity record);

    int updateStatusByInstanceId(GrayInstanceEntity record);

    int updatePolicyGroupByInstanceId(GrayInstanceEntity record);

    GrayPolicyGroupEntity selectPolicyGroupByInstanceId(String instanceId);
}