package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

@Data
public class GrayInstanceEntity {
    private Integer id;

    private String instanceId;

    private String serviceId;

    private Integer openGray;
}