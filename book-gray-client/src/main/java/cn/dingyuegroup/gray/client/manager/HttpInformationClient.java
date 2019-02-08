package cn.dingyuegroup.gray.client.manager;

import cn.dingyuegroup.gray.client.context.GrayClientAppContext;
import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayService;
import cn.dingyuegroup.gray.core.InformationClient;
import cn.dingyuegroup.gray.core.InstanceLocalInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        String url = this.baseUrl + "/gray/api/services/enable";
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
    public Boolean uploadInstanceLocalInfo() {
        String url = this.baseUrl + "/gray/api/services/uploadInstanceInfo?serviceId={1}&instanceId={2}&env={3}";
        try {
            InstanceLocalInfo localInfo = GrayClientAppContext.getInstanceLocalInfo();
            if (localInfo == null || StringUtils.isEmpty(localInfo.getServiceId()) || StringUtils.isEmpty(localInfo.getInstanceId())) {
                log.error("上传资源环境出错，数据不全:{}", localInfo);
                return false;
            }
            if (GrayClientAppContext.getEnvironment() == null || GrayClientAppContext.getEnvironment().getActiveProfiles().length == 0) {
                log.error("上传资源环境出错，资源数据不全");
                return false;
            }
            rest.getForEntity(url, Void.class, localInfo.getServiceId(), localInfo.getInstanceId(), GrayClientAppContext.getEnvironment().getActiveProfiles()[0]);
        } catch (RuntimeException e) {
            log.error("上传资源环境出错", e);
            throw e;
        }
        return false;
    }

    @Override
    public GrayService grayService(String serviceId) {
        List<GrayService> list = listGrayService();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Optional<GrayService> optional = list.parallelStream().filter(e -> e.getServiceId().equals(serviceId)).findAny();
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    @Override
    public GrayInstance grayInstance(String serviceId, String instanceId) {
        GrayService grayService = grayService(serviceId);
        if (grayService == null || CollectionUtils.isEmpty(grayService.getGrayInstances())) {
            return null;
        }
        Optional<GrayInstance> optional = grayService.getGrayInstances().parallelStream().filter(e -> e.getInstanceId().equals(instanceId)).findAny();
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    @Override
    public void addGrayInstance(String serviceId, String instanceId) {
        String url = this.baseUrl + "/gray/api/services/instance/online?serviceId={1}&instanceId={2}";
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
        serviceDownline(localInfo.getServiceId(), localInfo.getInstanceId());
    }

    @Override
    public void serviceDownline(String serviceId, String instanceId) {
        String url = this.baseUrl + "/gray/api/services/instance/offline";
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
