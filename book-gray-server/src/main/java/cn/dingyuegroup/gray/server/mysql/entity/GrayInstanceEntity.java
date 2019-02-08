package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

import java.util.Date;

@Data
public class GrayInstanceEntity {
    private Integer id;

    private String instanceId;

    private String serviceId;

    private Integer openGray;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private String policyGroupId;

    private String remark;

    private String env;
}