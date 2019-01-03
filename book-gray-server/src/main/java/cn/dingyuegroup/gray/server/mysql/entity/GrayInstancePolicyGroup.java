package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

@Data
public class GrayInstancePolicyGroup {
    private Integer id;

    private String instanceId;

    private String policyGroupId;
}