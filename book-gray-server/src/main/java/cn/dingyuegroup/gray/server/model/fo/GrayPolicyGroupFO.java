package cn.dingyuegroup.gray.server.model.fo;

import cn.dingyuegroup.gray.core.GrayPolicy;
import cn.dingyuegroup.gray.core.GrayPolicyGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel
@Data
public class GrayPolicyGroupFO {
    private String instanceId;
    private String policyGroupId;
    private String alias;
    private List<GrayPolicy> policies;
    @ApiModelProperty("0:关闭, 1:启用")
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
