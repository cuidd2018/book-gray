package cn.dingyuegroup.gray.server.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gray.server")
public class GrayServerConfigBean implements GrayServerConfig {

    private int evictInterval = 30000;

    @Override
    public int getEvictionIntervalTimerInMs() {
        return evictInterval;
    }

    public void setEvictInterval(int evictInterval) {
        this.evictInterval = evictInterval;
    }
}
