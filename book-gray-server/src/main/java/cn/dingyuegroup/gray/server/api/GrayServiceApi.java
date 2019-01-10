package cn.dingyuegroup.gray.server.api;

import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayPolicy;
import cn.dingyuegroup.gray.core.GrayPolicyGroup;
import cn.dingyuegroup.gray.core.GrayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/gray")
public interface GrayServiceApi {


    @RequestMapping(value = "/services", method = RequestMethod.GET)
    List<GrayService> services();

    @RequestMapping(value = "/services/enable", method = RequestMethod.GET)
    List<GrayService> enableServices();


    @RequestMapping(value = "/services/{serviceId}", method = RequestMethod.GET)
    GrayService service(@PathVariable("serviceId") String serviceId);


    @RequestMapping(value = "/services/{serviceId}/instances", method = RequestMethod.GET)
    List<GrayInstance> instances(@PathVariable("serviceId") String serviceId);


    @RequestMapping(value = "/services/{serviceId}/instance", method = RequestMethod.GET)
    GrayInstance getInstance(@PathVariable("serviceId") String serviceId,
                             @RequestParam("instanceId") String instanceId);


    @RequestMapping(value = "/services/{serviceId}/instance", method = RequestMethod.DELETE)
    ResponseEntity<Void> delInstance(@PathVariable("serviceId") String serviceId,
                                     @RequestParam("instanceId") String instanceId);

    @RequestMapping(value = "/services/instance/add", method = RequestMethod.GET)
    ResponseEntity<Void> addInstance(@RequestParam("serviceId") String serviceId, @RequestParam String instanceId);


    @RequestMapping(value = "/services/{serviceId}/instance/policyGroups", method = RequestMethod.GET)
    List<GrayPolicyGroup> policyGroups(@PathVariable("serviceId") String serviceId,
                                       @RequestParam("instanceId") String instanceId);


    @RequestMapping(value = "/services/{serviceId}/instance/policyGroups/{groupId}",
            method = RequestMethod.GET)
    GrayPolicyGroup policyGroup(@PathVariable("serviceId") String serviceId,
                                @RequestParam("instanceId") String instanceId,
                                @PathVariable("groupId") String groupId);


    @RequestMapping(value = "/services/{serviceId}/instance/policyGroups/{groupId}/policies",
            method = RequestMethod.GET)
    List<GrayPolicy> policies(@PathVariable("serviceId") String serviceId,
                              @RequestParam("instanceId") String instanceId,
                              @PathVariable("groupId") String groupId);


    @RequestMapping(value = "/services/{serviceId}/instance/policyGroups/{groupId}/policies/{policyId}",
            method = RequestMethod.GET)
    GrayPolicy policy(@PathVariable("serviceId") String serviceId,
                      @RequestParam("instanceId") String instanceId,
                      @PathVariable("groupId") String groupId,
                      @PathVariable("policyId") String policyId);
}
