package cn.dingyuegroup.gray.client.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gray.instance")
public class GrayInstanceProperties {

    private boolean enroll = true;
    private int enrollDealy = 10000;
    private boolean useMultiVersion = false;
    private String instanceId;

    /**
     * 是否使用多版本,默认不使用
     *
     * @return
     */
    public boolean isUseMultiVersion() {
        return useMultiVersion;
    }

    public void setUseMultiVersion(boolean useMultiVersion) {
        this.useMultiVersion = useMultiVersion;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public boolean isEnroll() {
        return enroll;
    }

    public void setEnroll(boolean enroll) {
        this.enroll = enroll;
    }

    public int getEnrollDealy() {
        return enrollDealy;
    }

    public void setEnrollDealy(int enrollDealy) {
        this.enrollDealy = enrollDealy;
    }
}
