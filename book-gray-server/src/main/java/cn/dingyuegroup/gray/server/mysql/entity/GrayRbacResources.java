package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class GrayRbacResources {
    private Integer id;

    private String resourceId;

    private String resourceName;

    private String resource;

    public static String genId() {
        return "RESOURCE_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
}