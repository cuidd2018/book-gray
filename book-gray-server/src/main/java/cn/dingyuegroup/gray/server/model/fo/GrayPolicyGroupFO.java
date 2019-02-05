package cn.dingyuegroup.gray.server.model.fo;

import cn.dingyuegroup.gray.core.GrayPolicy;
import cn.dingyuegroup.gray.core.GrayPolicyGroup;
import lombok.Data;

import java.util.List;

@Data
public class GrayPolicyGroupFO {
    private String instanceId;
    private String policyGroupId;
    private String alias;
    private List<GrayPolicy> policies;
    private boolean enable;

    public GrayPolicyGroup toGrayPolicyGroup() {
        GrayPolicyGroup policyGroup = new GrayPolicyGroup();
        policyGroup.setAlias(this.getAlias());
        policyGroup.setList(this.getPolicies());
        policyGroup.setEnable(this.isEnable());
        policyGroup.setPolicyGroupId(this.getPolicyGroupId());
        return policyGroup;
    }
}
