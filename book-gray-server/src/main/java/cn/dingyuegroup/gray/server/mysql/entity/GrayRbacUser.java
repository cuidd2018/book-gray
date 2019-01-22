package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

@Data
public class GrayRbacUser {
    private Integer id;

    private String udid;

    private String nickname;

    private String remark;

    private String departmentId;
}