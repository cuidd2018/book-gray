package cn.dingyuegroup.gray.server.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class GrayServiceVO {

    @ApiModelProperty("服务名")
    private String appName;

    @ApiModelProperty("服务id")
    private String serviceId;

    @ApiModelProperty("服务实例数")
    private int instanceSize;

    @ApiModelProperty("是否拥有灰度实例")
    private boolean hasGrayInstances;

    @ApiModelProperty("是否拥有灰度策略")
    private boolean hasGrayPolicies;
}
