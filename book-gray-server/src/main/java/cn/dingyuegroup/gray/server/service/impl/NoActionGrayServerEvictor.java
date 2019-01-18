package cn.dingyuegroup.gray.server.service.impl;


import cn.dingyuegroup.gray.server.service.GrayServerEvictor;
import cn.dingyuegroup.gray.server.manager.GrayServiceManager;

public class NoActionGrayServerEvictor implements GrayServerEvictor {


    public static NoActionGrayServerEvictor INSTANCE = new NoActionGrayServerEvictor();


    private NoActionGrayServerEvictor() {

    }

    @Override
    public void evict(GrayServiceManager serviceManager) {

    }
}
