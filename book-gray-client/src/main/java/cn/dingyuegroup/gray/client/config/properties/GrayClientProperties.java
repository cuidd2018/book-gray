package cn.dingyuegroup.gray.client.config.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationProperties(prefix = "gray.client")
@EnableConfigurationProperties(GrayInstanceProperties.class)
public class GrayClientProperties implements GrayClientConfig {

    @Autowired
    private GrayInstanceProperties instance;

    private int pullInteval = 30000;
    private String serverName;

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

    public GrayInstanceProperties getInstance() {
        return instance;
    }

    public void setInstance(GrayInstanceProperties instance) {
        this.instance = instance;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
