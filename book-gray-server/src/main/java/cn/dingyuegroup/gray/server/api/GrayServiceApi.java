package cn.dingyuegroup.gray.server.api;

import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayPolicy;
import cn.dingyuegroup.gray.core.GrayPolicyGroup;
import cn.dingyuegroup.gray.core.GrayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/gray")
public interface GrayServiceApi {

    @RequestMapping(value = "/services/enable", method = RequestMethod.GET)
    List<GrayService> enableServices();


    @RequestMapping(value = "/services/get", method = RequestMethod.GET)
    GrayService service(@RequestParam("serviceId") String serviceId);

    @RequestMapping(value = "/services/instance/get", method = RequestMethod.GET)
    GrayInstance getInstance(@RequestParam("serviceId") String serviceId,
                             @RequestParam("instanceId") String instanceId);


    @RequestMapping(value = "/services/instance/offline", method = RequestMethod.GET)
    ResponseEntity<Void> offlineInstance(@RequestParam("serviceId") String serviceId,
                                     @RequestParam("instanceId") String instanceId);

    @RequestMapping(value = "/services/instance/online", method = RequestMethod.GET)
    ResponseEntity<Void> onlineInstance(@RequestParam("serviceId") String serviceId, @RequestParam String instanceId);
}
