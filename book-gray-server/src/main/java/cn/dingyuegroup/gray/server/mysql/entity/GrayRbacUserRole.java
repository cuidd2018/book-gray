package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

@Data
public class GrayRbacUserRole {
    private Integer id;

    private String udid;

    private String roleId;

    //非数据表字段
    private String oldRoleId;
}