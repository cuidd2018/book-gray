package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class GrayRbacDepartment {
    private Integer id;

    private String departmentId;

    private String departmentName;

    private String creator;

    public static String genId() {
        return "DEPARTMENT_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
}