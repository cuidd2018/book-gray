package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayService;
import java.util.List;

public interface GrayServiceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GrayService record);

    GrayService selectByPrimaryKey(Integer id);

    List<GrayService> selectAll();

    int updateByPrimaryKey(GrayService record);
}