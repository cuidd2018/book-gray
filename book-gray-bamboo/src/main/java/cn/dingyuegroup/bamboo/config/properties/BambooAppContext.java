package cn.dingyuegroup.bamboo.config.properties;

import cn.dingyuegroup.bamboo.BambooRibbonConnectionPoint;
import cn.dingyuegroup.bamboo.ribbon.EurekaServerExtractor;

public class BambooAppContext {

    private static BambooRibbonConnectionPoint defaultConnectionPoint;
    private static EurekaServerExtractor eurekaServerExtractor;
    private static String localIp;

    public static BambooRibbonConnectionPoint getBambooRibbonConnectionPoint() {
        return defaultConnectionPoint;
    }


    public static void setDefaultConnectionPoint(BambooRibbonConnectionPoint connectionPoint) {
        BambooAppContext.defaultConnectionPoint = connectionPoint;
    }

    public static EurekaServerExtractor getEurekaServerExtractor() {
        return eurekaServerExtractor;
    }

    public static void setEurekaServerExtractor(EurekaServerExtractor eurekaServerExtractor) {
        BambooAppContext.eurekaServerExtractor = eurekaServerExtractor;
    }

    public static String getLocalIp() {
        return localIp;
    }

    public static void setLocalIp(String localIp) {
        BambooAppContext.localIp = localIp;
    }
}
