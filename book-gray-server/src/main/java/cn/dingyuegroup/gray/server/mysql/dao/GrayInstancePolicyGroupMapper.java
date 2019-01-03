package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayInstancePolicyGroup;
import java.util.List;

public interface GrayInstancePolicyGroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GrayInstancePolicyGroup record);

    GrayInstancePolicyGroup selectByPrimaryKey(Integer id);

    List<GrayInstancePolicyGroup> selectAll();

    int updateByPrimaryKey(GrayInstancePolicyGroup record);
}