package cn.dingyuegroup.gray.server.api.impl;

import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayPolicy;
import cn.dingyuegroup.gray.core.GrayPolicyGroup;
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
    public List<GrayService> services() {
        List<GrayService> grayServices = grayServiceManager.getServices();
        return new ArrayList<>(grayServices);
    }

    @Override
    public List<GrayService> enableServices() {
        Collection<GrayService> grayServices = grayServiceManager.getServices();
        List<GrayService> serviceList = new ArrayList<>(grayServices.size());
        for (GrayService grayService : grayServices) {
            if (grayService.isOpenGray()) {
                serviceList.add(grayService.takeNewOpenGrayService());
            }
        }
        return serviceList;
    }

    @Override
    public GrayService service(@PathVariable("serviceId") String serviceId) {
        return grayServiceManager.getGrayService(serviceId);
    }

    @Override
    public List<GrayInstance> instances(@PathVariable("serviceId") String serviceId) {
        return grayServiceManager.getGrayService(serviceId).getGrayInstances();
    }

    @Override
    public GrayInstance getInstance(@PathVariable("serviceId") String serviceId, String instanceId) {
        return grayServiceManager.getGrayInstance(serviceId, instanceId);
    }

    @Override
    public ResponseEntity<Void> delInstance(@PathVariable("serviceId") String serviceId, String instanceId) {
        //grayServiceManager.deleteGrayInstance(serviceId, instanceId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> addInstance(@RequestParam("serviceId") String serviceId, @RequestParam String instanceId) {
        grayServiceManager.editInstanceStatus(serviceId, instanceId, 0);
        return ResponseEntity.ok().build();
    }


    @Override
    public List<GrayPolicyGroup> policyGroups(@PathVariable("serviceId") String serviceId, String instanceId) {
        return grayServiceManager.getGrayInstance(serviceId, instanceId).getGrayPolicyGroups();
    }

    @Override
    public GrayPolicyGroup policyGroup(@PathVariable("serviceId") String serviceId, String instanceId,
                                       @PathVariable("groupId") String groupId) {
        return grayServiceManager.getGrayInstance(serviceId, instanceId).getGrayPolicyGroup(groupId);
    }

    @Override
    public List<GrayPolicy> policies(@PathVariable("serviceId") String serviceId, String instanceId,
                                     @PathVariable("groupId") String groupId) {
        return grayServiceManager.getGrayInstance(serviceId, instanceId).getGrayPolicyGroup(groupId).getList();
    }

    @Override
    public GrayPolicy policy(@PathVariable("serviceId") String serviceId, String instanceId,
                             @PathVariable("groupId") String groupId, @PathVariable("policyId") String policyId) {
        return grayServiceManager.getGrayInstance(serviceId, instanceId).getGrayPolicyGroup(groupId).getGrayPolicy(policyId);
    }
}
