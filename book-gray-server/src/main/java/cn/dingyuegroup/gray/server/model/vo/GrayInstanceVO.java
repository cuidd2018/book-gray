package cn.dingyuegroup.gray.server.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class GrayInstanceVO {

    private String serviceId;
    private String instanceId;
    private String appName;
    private String url;
    private Map<String, String> metadata;
    private boolean hasGrayPolicies;
    private boolean openGray;
    private boolean status;//在线状态
    private boolean eurekaStatus;//该实例在服务治理服务上的注册状态
    private String remark;
    private String policyGroupId;
    private String policyGroupAlias;
    private String envName;//资源环境
}
