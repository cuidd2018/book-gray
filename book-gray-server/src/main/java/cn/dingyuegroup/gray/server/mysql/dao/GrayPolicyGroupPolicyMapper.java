package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayPolicyGroupPolicy;

import java.util.List;

public interface GrayPolicyGroupPolicyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GrayPolicyGroupPolicy record);

    GrayPolicyGroupPolicy selectByPrimaryKey(Integer id);

    List<GrayPolicyGroupPolicy> selectAll();

    int updateByPrimaryKey(GrayPolicyGroupPolicy record);

    List<GrayPolicyGroupPolicy> selectByPolicyGroupId(String policyGroupId);
}