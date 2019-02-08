package cn.dingyuegroup.gray.client.context;

import cn.dingyuegroup.gray.client.config.properties.GrayClientProperties;
import cn.dingyuegroup.gray.core.GrayManager;
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

    public static Environment getEnvironment() {
        return environment;
    }

    public static void setEnvironment(Environment environment) {
        GrayClientAppContext.environment = environment;
    }
}
