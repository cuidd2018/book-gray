package cn.dingyuegroup.client.b.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "client-a")
@RequestMapping("/api/test")
public interface TestClient {

    @RequestMapping(path = "/api/test/get", method = RequestMethod.GET)
    Map<String, String> testGet(@RequestParam(value = "version", required = false) String version);

}
