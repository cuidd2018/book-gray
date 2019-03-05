package cn.dingyuegroup.gray.client.manager;

import cn.dingyuegroup.gray.client.config.properties.GrayClientProperties;
import cn.dingyuegroup.gray.client.context.GrayClientAppContext;
import cn.dingyuegroup.gray.core.GrayService;
import cn.dingyuegroup.gray.core.InstanceLocalInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class HttpInformationClient implements InformationClient {
    private static final Logger log = LoggerFactory.getLogger(HttpInformationClient.class);
    private final String serverName;
    private RestTemplate rest;

    public HttpInformationClient(String serverName, RestTemplate rest) {
        this.serverName = "http://" + serverName;
        this.rest = rest;
    }

    @Override
    public List<GrayService> listGrayService() {
        log.info("\n----------------------------------------------------------\n\t"
                        + "pull instances from gray server:\n\t"
                        + "Local service: \t\t{}\n\t"
                        + "Local instance: \t\t{}\n\t"
                        + "gray service: \t\t{}\n----------------------------------------------------------",
                GrayClientAppContext.getInstanceLocalInfo().getServiceId(), GrayClientAppContext.getInstanceLocalInfo().getInstanceId(), serverName);
        String url = this.serverName + "/gray/api/services/enable";
        ParameterizedTypeReference<List<GrayService>> typeRef = new ParameterizedTypeReference<List<GrayService>>() {
        };
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(GrayClientProperties.Header.COS.getKey(), GrayClientProperties.Header.COS.getValue());
            HttpEntity<MultiValueMap> requestEntity = new HttpEntity<>(null, headers);
            ResponseEntity<List<GrayService>> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, typeRef);
            log.info("灰度服务列表:{}", responseEntity.getBody() == null ? null : responseEntity.getBody().toString());
            log.info("\n" + "gray list: \t\t{}\n----------------------------------------------------------",
                    responseEntity.getBody() == null ? null : responseEntity.getBody().toString());
            return responseEntity.getBody();
        } catch (RuntimeException e) {
            log.error("获取灰度服务列表失败", e);
            return null;
        }
    }

    @Override
    public Boolean uploadInstanceLocalInfo() {
        log.info("\n----------------------------------------------------------\n\t"
                + "upload service enviroment to gray server\n\t----------------------------------------------------------");
        String url = this.serverName + "/gray/api/services/uploadInstanceInfo";
        try {
            InstanceLocalInfo localInfo = GrayClientAppContext.getInstanceLocalInfo();
            if (localInfo == null || StringUtils.isEmpty(localInfo.getServiceId()) || StringUtils.isEmpty(localInfo.getInstanceId())) {
                log.error("upload service enviroment to gray error,localInfo data missing:{}", localInfo);
                return false;
            }
            if (GrayClientAppContext.getEnvironment() == null || GrayClientAppContext.getEnvironment().getActiveProfiles().length == 0) {
                log.error("upload service enviroment to gray error,enviroment data missing:{}", localInfo);
                return false;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add(GrayClientProperties.Header.COS.getKey(), GrayClientProperties.Header.COS.getValue());
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("serviceId", localInfo.getServiceId());
            requestBody.add("instanceId", localInfo.getInstanceId());
            requestBody.add("env", GrayClientAppContext.getEnvironment().getActiveProfiles()[0]);
            HttpEntity<MultiValueMap> requestEntity = new HttpEntity<>(requestBody, headers);
            rest.exchange(url, HttpMethod.GET, requestEntity, Void.class);
            log.info("\n----------------------------------------------------------\n\t"
                    + "upload service enviroment to gray server done\n\t----------------------------------------------------------");
        } catch (RuntimeException e) {
            log.info("upload service enviroment to gray error", e);
            throw e;
        }
        return false;
    }

    @Override
    public void addGrayInstance(String serviceId, String instanceId) {
        String url = this.serverName + "/gray/api/services/instance/online";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(GrayClientProperties.Header.COS.getKey(), GrayClientProperties.Header.COS.getValue());
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("serviceId", serviceId);
            requestBody.add("instanceId", instanceId);
            HttpEntity<MultiValueMap> requestEntity = new HttpEntity<>(requestBody, headers);
            rest.exchange(url, HttpMethod.GET, requestEntity, Void.class);
        } catch (RuntimeException e) {
            log.error("灰度服务实例下线失败", e);
            throw e;
        }
    }

    @Override
    public void serviceDownline() {
        InstanceLocalInfo localInfo = GrayClientAppContext.getInstanceLocalInfo();
        String url = this.serverName + "/gray/api/services/instance/offline";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(GrayClientProperties.Header.COS.getKey(), GrayClientProperties.Header.COS.getValue());
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("serviceId", localInfo.getServiceId());
            requestBody.add("instanceId", localInfo.getInstanceId());
            HttpEntity<MultiValueMap> requestEntity = new HttpEntity<>(requestBody, headers);
            rest.exchange(url, HttpMethod.GET, requestEntity, Void.class);
        } catch (Exception e) {
            log.error("灰度服务实例下线失败", e);
            throw e;
        }
    }
}
