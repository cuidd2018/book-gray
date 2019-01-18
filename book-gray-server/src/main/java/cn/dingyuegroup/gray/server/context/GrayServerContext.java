package cn.dingyuegroup.gray.server.context;


import cn.dingyuegroup.gray.server.service.GrayServerEvictor;
import cn.dingyuegroup.gray.server.manager.GrayServiceManager;

public class GrayServerContext {

    public static final String DEFAULT_PREFIX = "gray";

    private static GrayServiceManager grayServiceManager;
    private static GrayServerEvictor grayServerEvictor;

    public static GrayServiceManager getGrayServiceManager() {
        return grayServiceManager;
    }

    static void setGrayServiceManager(GrayServiceManager grayServiceManager) {
        GrayServerContext.grayServiceManager = grayServiceManager;
    }

    public static GrayServerEvictor getGrayServerEvictor() {
        return grayServerEvictor;
    }

    static void setGrayServerEvictor(GrayServerEvictor grayServerEvictor) {
        GrayServerContext.grayServerEvictor = grayServerEvictor;
    }
}
