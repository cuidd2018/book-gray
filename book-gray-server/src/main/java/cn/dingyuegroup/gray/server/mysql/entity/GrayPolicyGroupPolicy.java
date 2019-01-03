package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

@Data
public class GrayPolicyGroupPolicy {
    private Integer id;

    private String policyGroupId;

    private String policyId;
}