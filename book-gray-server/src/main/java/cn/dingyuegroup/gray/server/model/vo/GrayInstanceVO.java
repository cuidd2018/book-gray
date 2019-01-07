package cn.dingyuegroup.gray.server.model.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
