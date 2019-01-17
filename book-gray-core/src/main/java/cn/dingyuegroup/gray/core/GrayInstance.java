package cn.dingyuegroup.gray.core;

import lombok.Data;
import lombok.ToString;

import java.util.Map;


/**
 * 灰度实例，有状态属性
 */
@ToString
@Data
public class GrayInstance {
    private String appName;
    private String serviceId;
    private String instanceId;

    /**
     * 类度策略组
     */
    private GrayPolicyGroup grayPolicyGroup;
    private boolean openGray = true;
    private boolean status = false;//是否在线
    private String url;
    private Map<String, String> metadata;

    public GrayInstance() {
    }

    public boolean hasGrayPolicy() {
        if (getGrayPolicyGroup() != null) {
            return true;
        }
        return false;
    }

    public GrayInstance toNewGrayInstance() {
        GrayInstance newInstance = new GrayInstance();
        newInstance.setInstanceId(instanceId);
        newInstance.setServiceId(serviceId);
        newInstance.setOpenGray(openGray);
        newInstance.setStatus(status);
        newInstance.setAppName(appName);
        newInstance.setUrl(url);
        newInstance.setMetadata(metadata);
        return newInstance;
    }

    public GrayInstance takeNewOpenGrayInstance() {
        GrayInstance instance = toNewGrayInstance();
        if (grayPolicyGroup.isEnable()) {
            instance.setGrayPolicyGroup(grayPolicyGroup);
        }
        return instance;
    }
}
