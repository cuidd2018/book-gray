package cn.dingyuegroup.gray.server.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gray.server")
public class GrayServerConfigBean implements GrayServerConfig {

    private int evictionIntervalTimerInMs = 30000;

    @Override
    public int getEvictionIntervalTimerInMs() {
        return evictionIntervalTimerInMs;
    }
}
