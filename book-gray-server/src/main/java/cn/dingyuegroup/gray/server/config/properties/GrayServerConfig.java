package cn.dingyuegroup.gray.server.config.properties;

public interface GrayServerConfig {

    /**
     * 检查服务实例是否下线的间隔时间(ms)
     *
     * @return 返回服务实例是否下线的间隔时间(ms)
     */
    int getEvictionIntervalTimerInMs();
}
