package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class GrayRbacUser {
    private Integer id;

    private String udid;

    private String nickname;

    private String remark;

    private String departmentId;

    public static String genId() {
        return "USER_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
}