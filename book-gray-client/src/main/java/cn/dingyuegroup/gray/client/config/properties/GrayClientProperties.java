package cn.dingyuegroup.gray.client.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("gray.client")
public class GrayClientProperties implements GrayClientConfig {


    private int serviceUpdateIntervalTimerInMs = 30000;

    private String serverName;

    private InstanceConfig instance = new InstanceConfig();

    @Override
    public boolean isGrayEnroll() {
        return instance.isGrayEnroll();
    }

    @Override
    public int grayEnrollDealyTimeInMs() {
        return instance.getGrayEnrollDealyTimeInMs();
    }

    @Override
    public int getServiceUpdateIntervalTimerInMs() {
        return serviceUpdateIntervalTimerInMs;
    }


    public void setServiceUpdateIntervalTimerInMs(int serviceUpdateIntervalTimerInMs) {
        this.serviceUpdateIntervalTimerInMs = serviceUpdateIntervalTimerInMs;
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
    public class InstanceConfig {

        private boolean grayEnroll = false;
        private int grayEnrollDealyTimeInMs = 40000;
        private boolean useMultiVersion = false;
        private String instanceId;

        public boolean isGrayEnroll() {
            return grayEnroll;
        }

        public void setGrayEnroll(boolean grayEnroll) {
            this.grayEnroll = grayEnroll;
        }

        public int getGrayEnrollDealyTimeInMs() {
            return grayEnrollDealyTimeInMs;
        }

        public void setGrayEnrollDealyTimeInMs(int grayEnrollDealyTimeInMs) {
            this.grayEnrollDealyTimeInMs = grayEnrollDealyTimeInMs;
        }

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
    }
}
