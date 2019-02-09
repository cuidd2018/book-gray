package cn.dingyuegroup.gray.server.model.vo;


import lombok.Data;

@Data
public class GrayPolicyVO {
    //策略组属性
    private String policyGroupId;

    private String alias;

    private String groupType;
    //策略属性
    private String policyId;

    private String policyType;

    private String policyKey;

    private String policyValue;

    private String policyMatchType;

    private String remark;

    private String policyName;

    private String creator;

    private String creatorName;//创建者名称nickname
}
