package cn.dingyuegroup.gray.client.config.properties;

public interface GrayClientConfig {


    /**
     * 启动时是否灰度注册
     *
     * @return boolean
     */
    boolean isGrayEnroll();

    /**
     * 向灰度服务器注册的延迟时间(ms)
     *
     * @return 返回灰度服务器注册的延迟时间(ms)
     */
    int grayEnrollDealyTimeInMs();


    /**
     * 灰度服务器的url
     *
     * @return 返回gray-server的url
     */
    String getServerUrl();


    /**
     * 更新灰度列表的时间间隔(ms)
     *
     * @return 返回更新灰度列表的时间间隔(ms)
     */
    int getServiceUpdateIntervalTimerInMs();


    /**
     * 在和灰度服务器通信时，如果交互失败，是否重试。
     *
     * @return 返回是否重试
     */
    boolean isRetryable();

    /**
     * 重试次数
     *
     * @return 返回重试次数
     */
    int getRetryNumberOfRetries();

}
