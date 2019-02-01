package cn.dingyuegroup.gray.server.web;

import cn.dingyuegroup.gray.server.config.GrayMQMarkerConfiguration;
import cn.dingyuegroup.gray.server.manager.SendMessageManager;
import cn.dingyuegroup.gray.server.web.base.BaseController;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 170147 on 2019/1/18.
 */
@RestController
@RequestMapping("/gray/manager/refresh")
@ConditionalOnBean(GrayMQMarkerConfiguration.GrayMQMarker.class)
public class GrayRefreshController extends BaseController {

    @Autowired
    private SendMessageManager sendMessageManager;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Void> delPolicyGroup(
            @RequestParam("serviceId") String serviceId,
            @RequestParam("instanceId") String instanceId) {
        Map<String, String> map = new HashMap<>();
        map.put("serviceId", serviceId);
        map.put("instanceId", instanceId);
        sendMessageManager.sendMessage(JSON.toJSONString(map));
        return ResponseEntity.ok().build();
    }
}
