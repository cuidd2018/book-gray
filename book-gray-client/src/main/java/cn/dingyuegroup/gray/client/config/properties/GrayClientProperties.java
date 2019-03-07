package cn.dingyuegroup.gray.client.config.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gray.client")
public class GrayClientProperties implements GrayClientConfig {

    private int pullInteval = 30000;
    private String serverName;

    private InstanceConfig instance = new InstanceConfig();

    public enum Header {

        COS("category-of-service", "gray-server");

        String key;

        String value;

        Header(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    public boolean isGrayEnroll() {
        return instance.isEnroll();
    }

    @Override
    public int grayEnrollDealyTimeInMs() {
        return instance.getEnrollDealy();
    }

    @Override
    public int getServiceUpdateIntervalTimerInMs() {
        return pullInteval;
    }

    public void setPullInteval(Integer pullInteval) {
        this.pullInteval = pullInteval;
    }

    public InstanceConfig getInstance() {
        return instance;
    }

    public void setInstance(InstanceConfig instance) {
        this.instance = instance;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * 实例
     */
    @ConditionalOnProperty(prefix = "gray.instance")
    public class InstanceConfig {
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
}
