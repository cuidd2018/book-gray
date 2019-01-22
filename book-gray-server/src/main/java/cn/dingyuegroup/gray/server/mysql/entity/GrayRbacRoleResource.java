package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

@Data
public class GrayRbacRoleResource {
    private Integer id;

    private String resourceId;

    private String roleId;
}