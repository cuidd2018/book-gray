package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

@Data
public class GrayInstance {
    private Integer id;

    private String instanceId;

    private String serviceId;

    private Short openGray;
}