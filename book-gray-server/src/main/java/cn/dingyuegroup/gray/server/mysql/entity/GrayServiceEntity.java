package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

import java.util.Date;

@Data
public class GrayServiceEntity {

    private Integer id;

    private String serviceId;

    private String appName;

    private Date createTime;

    private Date updateTime;

    private Integer isDelete;

    private Integer sort;
}