package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayServiceEntity;

import java.util.List;

public interface GrayServiceMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(GrayServiceEntity record);

    GrayServiceEntity selectByPrimaryKey(Integer id);

    List<GrayServiceEntity> selectAll();

    int updateByPrimaryKey(GrayServiceEntity record);

    GrayServiceEntity selectByServiceId(String serviceId);

    int updateStatusByServiceId(GrayServiceEntity record);
}