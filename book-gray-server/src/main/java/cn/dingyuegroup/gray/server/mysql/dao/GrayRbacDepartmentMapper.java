package cn.dingyuegroup.gray.server.mysql.dao;

import cn.dingyuegroup.gray.server.mysql.entity.GrayRbacDepartment;

import java.util.List;

public interface GrayRbacDepartmentMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(GrayRbacDepartment record);

    GrayRbacDepartment selectByPrimaryKey(Integer id);

    List<GrayRbacDepartment> selectAll();

    int updateByPrimaryKey(GrayRbacDepartment record);

    GrayRbacDepartment selectByDepartmentId(String departmentId);

    int updateByDepartmentId(GrayRbacDepartment record);

    int deleteByDepartmentId(String departmentId);

    List<GrayRbacDepartment> selectByCreator(String creator);
}