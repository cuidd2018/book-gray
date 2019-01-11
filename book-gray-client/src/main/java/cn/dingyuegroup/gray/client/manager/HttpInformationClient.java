package cn.dingyuegroup.gray.client.manager;

import cn.dingyuegroup.gray.client.context.GrayClientAppContext;
import cn.dingyuegroup.gray.client.context.InstanceLocalInfo;
import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayService;
import cn.dingyuegroup.gray.core.InformationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpInformationClient implements InformationClient {
    private static final Logger log = LoggerFactory.getLogger(HttpInformationClient.class);
    private final String baseUrl;
    private RestTemplate rest;

    public HttpInformationClient(String baseUrl, RestTemplate rest) {
        this.baseUrl = baseUrl;
        this.rest = rest;
    }

    @Override
    public List<GrayService> listGrayService() {
        String url = this.baseUrl + "/gray/services/enable";
        ParameterizedTypeReference<List<GrayService>> typeRef = new ParameterizedTypeReference<List<GrayService>>() {
        };
        try {
            ResponseEntity<List<GrayService>> responseEntity = rest.exchange(url, HttpMethod.GET, null, typeRef);
            log.info("灰度服务列表:{}", responseEntity.getBody() == null ? null : responseEntity.getBody().toString());
            return responseEntity.getBody();
        } catch (RuntimeException e) {
            log.error("获取灰度服务列表失败", e);
            throw e;
        }
    }

    @Override
    public GrayService grayService(String serviceId) {
        String url = this.baseUrl + "/gray/services/get";
        Map<String, String> params = new HashMap<>();
        params.put("serviceId", serviceId);
        try {
            ResponseEntity<GrayService> responseEntity = rest.getForEntity(url, GrayService.class, params);
            log.info("获取灰度服务:serviceId:{},service:{}", serviceId, responseEntity.getBody() == null ? null : responseEntity.getBody().toString());
            return responseEntity.getBody();
        } catch (RuntimeException e) {
            log.error("获取灰度服务失败", e);
            throw e;
        }
    }

    @Override
    public GrayInstance grayInstance(String serviceId, String instanceId) {
        String url = this.baseUrl + "/gray/services/instance/get";
        Map<String, String> params = new HashMap<>();
        params.put("serviceId", serviceId);
        params.put("instanceId", instanceId);
        try {
            ResponseEntity<GrayInstance> responseEntity = rest.getForEntity(url, GrayInstance.class, params);
            log.info("获取灰度服务实例:serviceId:{},instanceId:{},instance:{}", serviceId, instanceId, responseEntity.getBody() == null ? null : responseEntity.getBody());
            return responseEntity.getBody();
        } catch (RuntimeException e) {
            log.error("获取灰度服务实例失败", e);
            throw e;
        }
    }

    @Override
    public void addGrayInstance(String serviceId, String instanceId) {
        String url = this.baseUrl + "/gray/services/instance/online?serviceId={1}&instanceId={2}";
        try {
            rest.getForEntity(url, Void.class, serviceId, instanceId);
        } catch (RuntimeException e) {
            log.error("灰度服务实例下线失败", e);
            throw e;
        }
    }

    @Override
    public void serviceDownline() {
        InstanceLocalInfo localInfo = GrayClientAppContext.getInstanceLocalInfo();
        if (!localInfo.isGray()) {
            return;
        }
        serviceDownline(localInfo.getServiceId(), localInfo.getInstanceId());
    }

    @Override
    public void serviceDownline(String serviceId, String instanceId) {
        String url = this.baseUrl + "/gray/services/instance/offline";
        try {
            Map<String, String> params = new HashMap<>();
            params.put("serviceId", serviceId);
            params.put("instanceId", instanceId);
            rest.getForEntity(url, Void.class, params);
        } catch (Exception e) {
            log.error("灰度服务实例下线失败", e);
            throw e;
        }
    }
}
