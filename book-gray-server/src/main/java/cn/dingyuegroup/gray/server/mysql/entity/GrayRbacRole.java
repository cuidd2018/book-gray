package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class GrayRbacRole {
    private Integer id;

    private String roleId;

    private String role;

    private String roleName;

    private String departmentId;

    private Integer isDepartmentAdmin;

    private String creator;

    public static String genId() {
        return "ROLE_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
}