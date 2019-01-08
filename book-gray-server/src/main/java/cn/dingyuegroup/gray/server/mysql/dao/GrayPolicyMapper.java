package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayPolicyEntity;

import java.util.List;

public interface GrayPolicyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GrayPolicyEntity record);

    GrayPolicyEntity selectByPrimaryKey(Integer id);

    List<GrayPolicyEntity> selectAll();

    int updateByPrimaryKey(GrayPolicyEntity record);

    List<GrayPolicyEntity> selectListByPolicyId(List<String> list);

    GrayPolicyEntity selectByPolicyId(String policyId);

    int updateByPolicyId(GrayPolicyEntity record);

    int deleteByPolicyId(String policyId);
}