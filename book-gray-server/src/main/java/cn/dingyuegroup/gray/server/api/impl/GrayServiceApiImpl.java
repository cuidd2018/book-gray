package cn.dingyuegroup.gray.server.api.impl;

import cn.dingyuegroup.gray.core.GrayService;
import cn.dingyuegroup.gray.server.api.GrayServiceApi;
import cn.dingyuegroup.gray.server.manager.GrayServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
public class GrayServiceApiImpl implements GrayServiceApi {
    @Autowired
    private GrayServiceManager grayServiceManager;

    @Override
    public List<GrayService> enableServices() {
        Collection<GrayService> grayServices = grayServiceManager.getServices();
        if (grayServices == null) {
            grayServices = new ArrayList<>();
        }
        List<GrayService> serviceList = new ArrayList<>(grayServices.size());
        for (GrayService grayService : grayServices) {
            //在线 && 开启灰度
            if (grayService.isStatus() && grayService.isOpenGray()) {
                serviceList.add(grayService.takeNewOpenGrayService());
            }
        }
        return serviceList;
    }

    @Override
    public ResponseEntity<Void> offlineInstance(@PathVariable("serviceId") String serviceId, @RequestParam("instanceId") String instanceId) {
        grayServiceManager.editInstanceOnlineStatus(serviceId, instanceId, 0);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> onlineInstance(@RequestParam("serviceId") String serviceId, @RequestParam("instanceId") String instanceId) {
        grayServiceManager.editInstanceOnlineStatus(serviceId, instanceId, 1);
        return ResponseEntity.ok().build();
    }
}
