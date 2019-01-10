package cn.dingyuegroup.gray.server.service.impl;

import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayService;
import cn.dingyuegroup.gray.server.manager.GrayServiceManager;
import cn.dingyuegroup.gray.server.service.GrayServerEvictor;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

import java.util.Collection;


/**
 * 依赖EurekaClient来检查服务实例是否下线
 */
public class EurekaGrayServerEvictor implements GrayServerEvictor {

    private EurekaClient eurekaClient;


    public EurekaGrayServerEvictor(EurekaClient eurekaClient) {
        this.eurekaClient = eurekaClient;
    }

    @Override
    public void evict(GrayServiceManager serviceManager) {
        Collection<GrayService> grayServices = serviceManager.getServices();
        grayServices.forEach(grayService -> {
            grayService.getGrayInstances().forEach(grayInstance -> {
                evict(serviceManager, grayInstance);
            });
        });

    }


    private void evict(GrayServiceManager serviceManager, GrayInstance grayInstance) {
        if (isDownline(grayInstance)) {
            //serviceManager.deleteGrayInstance(grayInstance.getServiceId(), grayInstance.getInstanceId());
        }
    }


    private boolean isDownline(GrayInstance grayInstance) {
        Application app = eurekaClient.getApplication(grayInstance.getServiceId());
        return app == null || app.getByInstanceId(grayInstance.getInstanceId()) == null;
    }

}
