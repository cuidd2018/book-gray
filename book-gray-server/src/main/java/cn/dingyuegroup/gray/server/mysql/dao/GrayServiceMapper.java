package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayServiceEntity;

import java.util.List;

public interface GrayServiceMapper {

    int deleteByServiceId(String serviceId);

    int insert(GrayServiceEntity record);

    GrayServiceEntity selectByPrimaryKey(Integer id);

    List<GrayServiceEntity> selectAll();

    int updateByPrimaryKey(GrayServiceEntity record);

    GrayServiceEntity selectByServiceId(String serviceId);

    void updateByServiceId(GrayServiceEntity record);
}