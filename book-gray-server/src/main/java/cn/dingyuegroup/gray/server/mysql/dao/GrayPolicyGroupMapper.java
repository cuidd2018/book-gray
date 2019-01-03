package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayPolicyGroup;
import java.util.List;

public interface GrayPolicyGroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GrayPolicyGroup record);

    GrayPolicyGroup selectByPrimaryKey(Integer id);

    List<GrayPolicyGroup> selectAll();

    int updateByPrimaryKey(GrayPolicyGroup record);
}