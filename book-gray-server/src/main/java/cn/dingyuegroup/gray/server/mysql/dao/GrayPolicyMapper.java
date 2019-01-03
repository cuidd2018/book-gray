package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayPolicy;
import java.util.List;

public interface GrayPolicyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GrayPolicy record);

    GrayPolicy selectByPrimaryKey(Integer id);

    List<GrayPolicy> selectAll();

    int updateByPrimaryKey(GrayPolicy record);
}