package cn.dingyuegroup.gray.server.model.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GrayServiceVO {

    private String appName;

    private String serviceId;

    private int instanceSize;

    private boolean hasGrayInstances;

    private boolean hasGrayPolicies;

    private boolean status;

    private String remark;//服务说明
}
