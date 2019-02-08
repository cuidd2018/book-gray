package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

@Data
public class GrayRbacResources {
    private Integer id;

    private String resourceId;

    private String resourceName;

    private String resource;
}