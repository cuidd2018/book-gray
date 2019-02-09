package cn.dingyuegroup.gray.server.model.vo;


import cn.dingyuegroup.gray.core.GrayPolicy;
import lombok.Data;

import java.util.List;

@Data
public class GrayPolicyGroupVO {
    private String serviceId;
    private String instanceId;
    private String appName;
    private String url;
    private String policyGroupId;
    private String alias;
    private List<GrayPolicy> policies;
    private boolean enable;
    private String groupType;
    private String remark;
    private String creator;
}
