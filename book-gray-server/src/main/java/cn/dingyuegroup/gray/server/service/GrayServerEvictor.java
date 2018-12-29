package cn.dingyuegroup.gray.server.service;


import cn.dingyuegroup.gray.core.GrayServiceManager;

/**
 *
 */
public interface GrayServerEvictor {

    void evict(GrayServiceManager serviceManager);
}
