package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayInstancePolicyGroup;
import cn.dingyuegroup.gray.server.mysql.entity.GrayPolicyGroupEntity;

import java.util.List;

public interface GrayInstancePolicyGroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GrayInstancePolicyGroup record);

    GrayInstancePolicyGroup selectByPrimaryKey(Integer id);

    List<GrayInstancePolicyGroup> selectAll();

    int updateByPrimaryKey(GrayInstancePolicyGroup record);

    List<GrayInstancePolicyGroup> selectByInstanceId(String instanceId);

    List<GrayPolicyGroupEntity> selectPolicyGroupByInstanceId(String instanceId);
}