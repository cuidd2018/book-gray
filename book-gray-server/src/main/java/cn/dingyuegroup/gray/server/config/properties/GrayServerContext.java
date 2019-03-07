package cn.dingyuegroup.gray.server.config.properties;


import cn.dingyuegroup.gray.core.InstanceLocalInfo;
import cn.dingyuegroup.gray.server.manager.GrayServiceManager;
import cn.dingyuegroup.gray.server.mysql.dao.GrayRbacUserMapper;
import cn.dingyuegroup.gray.server.service.GrayServerEvictor;
import org.springframework.core.env.Environment;

public class GrayServerContext {

    public static final String DEFAULT_PREFIX = "gray";

    private static GrayServiceManager grayServiceManager;
    private static GrayServerEvictor grayServerEvictor;
    private static GrayRbacUserMapper grayRbacUserMapper;
    private static InstanceLocalInfo instanceLocalInfo;
    private static Environment environment;

    public static GrayRbacUserMapper getGrayRbacUserMapper() {
        return grayRbacUserMapper;
    }

    public static void setGrayRbacUserMapper(GrayRbacUserMapper grayRbacUserMapper) {
        GrayServerContext.grayRbacUserMapper = grayRbacUserMapper;
    }

    public static GrayServiceManager getGrayServiceManager() {
        return grayServiceManager;
    }

    public static void setGrayServiceManager(GrayServiceManager grayServiceManager) {
        GrayServerContext.grayServiceManager = grayServiceManager;
    }

    public static GrayServerEvictor getGrayServerEvictor() {
        return grayServerEvictor;
    }

    public static void setGrayServerEvictor(GrayServerEvictor grayServerEvictor) {
        GrayServerContext.grayServerEvictor = grayServerEvictor;
    }

    public static Environment getEnvironment() {
        return environment;
    }

    public static void setEnvironment(Environment environment) {
        GrayServerContext.environment = environment;
    }

    public static InstanceLocalInfo getInstanceLocalInfo() {
        return instanceLocalInfo;
    }

    public static void setInstanceLocalInfo(InstanceLocalInfo instanceLocalInfo) {
        GrayServerContext.instanceLocalInfo = instanceLocalInfo;
    }
}
