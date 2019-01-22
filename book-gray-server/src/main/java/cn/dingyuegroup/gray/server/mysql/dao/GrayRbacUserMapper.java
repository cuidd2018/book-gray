package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayRbacUser;

import java.util.List;

public interface GrayRbacUserMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(GrayRbacUser record);

    GrayRbacUser selectByPrimaryKey(Integer id);

    List<GrayRbacUser> selectAll();

    int updateByPrimaryKey(GrayRbacUser record);

    GrayRbacUser selectByUdid(String udid);

    GrayRbacUser selectByDepartmentId(String departmentId);
}