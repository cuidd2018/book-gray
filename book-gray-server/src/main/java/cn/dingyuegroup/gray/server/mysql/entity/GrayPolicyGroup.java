package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

@Data
public class GrayPolicyGroup {
    private Integer id;

    private String policyGroupId;

    private String alias;

    private Short enable;

    private Short isDelete;
}