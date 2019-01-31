package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

@Data
public class GrayRbacRole {
    private Integer id;

    private String roleId;

    private String role;

    private String roleName;
}