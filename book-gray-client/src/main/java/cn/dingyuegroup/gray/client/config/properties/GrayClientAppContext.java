package cn.dingyuegroup.gray.client.config.properties;

import cn.dingyuegroup.gray.client.manager.GrayManager;
import cn.dingyuegroup.gray.core.InstanceLocalInfo;
import org.springframework.core.env.Environment;

public class GrayClientAppContext {
    private static GrayManager grayManager;
    private static InstanceLocalInfo instanceLocalInfo;
    private static GrayClientProperties grayClientProperties;
    private static Environment environment;

    public static GrayManager getGrayManager() {
        return grayManager;
    }

    public static void setGrayManager(GrayManager grayManager) {
        GrayClientAppContext.grayManager = grayManager;
    }


    public static InstanceLocalInfo getInstanceLocalInfo() {
        return instanceLocalInfo;
    }

    public static void setInstanceLocalInfo(InstanceLocalInfo instanceLocalInfo) {
        GrayClientAppContext.instanceLocalInfo = instanceLocalInfo;
    }

    public static GrayClientProperties getGrayClientProperties() {
        return grayClientProperties;
    }

    public static void setGrayClientProperties(GrayClientProperties grayClientProperties) {
        GrayClientAppContext.grayClientProperties = grayClientProperties;
    }

    public static Environment getEnvironment() {
        return environment;
    }

    public static void setEnvironment(Environment environment) {
        GrayClientAppContext.environment = environment;
    }
}
