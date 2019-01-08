package cn.dingyuegroup.gray.server.service;


import cn.dingyuegroup.gray.server.manager.GrayServiceManager;

/**
 *
 */
public interface GrayServerEvictor {

    void evict(GrayServiceManager serviceManager);
}
