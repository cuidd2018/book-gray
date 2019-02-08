package cn.dingyuegroup.gray.server.api;

import cn.dingyuegroup.gray.core.GrayService;
import cn.dingyuegroup.gray.core.InstanceLocalInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/gray/api")
public interface GrayServiceApi {

    @RequestMapping(value = "/services/enable", method = RequestMethod.GET)
    List<GrayService> enableServices();

    @RequestMapping(value = "/services/instance/offline", method = RequestMethod.GET)
    ResponseEntity<Void> offlineInstance(@RequestParam("serviceId") String serviceId, @RequestParam("instanceId") String instanceId);

    @RequestMapping(value = "/services/instance/online", method = RequestMethod.GET)
    ResponseEntity<Void> onlineInstance(@RequestParam("serviceId") String serviceId, @RequestParam("instanceId") String instanceId);

    @RequestMapping(value = "/services/uploadInstanceInfo", method = RequestMethod.GET)
    ResponseEntity<Void> uploadInstanceInfo(InstanceLocalInfo instanceLocalInfo);
}
