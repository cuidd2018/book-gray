package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

import java.util.Date;

@Data
public class GrayPolicyEntity {
    private Integer id;

    private String policyId;

    private String policyType;

    private String policy;

    private Short isDelete;

    private Date createTime;

    private Date updateTime;
}