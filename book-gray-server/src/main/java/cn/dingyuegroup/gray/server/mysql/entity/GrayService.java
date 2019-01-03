package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

import java.util.Date;

@Data
public class GrayService {
    private Integer id;

    private String serviceId;

    private Date createTime;

    private Date updateTime;

    private Short isDelete;
}