package cn.dingyuegroup.gray.client.context;

import cn.dingyuegroup.gray.client.config.properties.GrayClientProperties;
import cn.dingyuegroup.gray.core.GrayManager;

public class GrayClientAppContext {
    private static GrayManager grayManager;
    private static InstanceLocalInfo instanceLocalInfo;
    private static GrayClientProperties grayClientProperties;


    public static GrayManager getGrayManager() {
        return grayManager;
    }

    static void setGrayManager(GrayManager grayManager) {
        GrayClientAppContext.grayManager = grayManager;
    }


    public static InstanceLocalInfo getInstanceLocalInfo() {
        return instanceLocalInfo;
    }

    static void setInstanceLocalInfo(InstanceLocalInfo instanceLocalInfo) {
        GrayClientAppContext.instanceLocalInfo = instanceLocalInfo;
    }

    public static GrayClientProperties getGrayClientProperties() {
        return grayClientProperties;
    }

    static void setGrayClientProperties(GrayClientProperties grayClientProperties) {
        GrayClientAppContext.grayClientProperties = grayClientProperties;
    }
}
