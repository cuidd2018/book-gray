package cn.dingyuegroup.bamboo.ribbon;

import cn.dingyuegroup.bamboo.BambooAppContext;
import cn.dingyuegroup.bamboo.BambooRequest;
import cn.dingyuegroup.bamboo.ConnectPointContext;
import cn.dingyuegroup.bamboo.autoconfig.properties.BambooProperties;
import cn.dingyuegroup.bamboo.utils.WebUtils;
import cn.dingyuegroup.bamboo.web.RequestIpKeeper;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;


/**
 * 用于@LoadBalance 标记的 RestTemplate，主要作用是用来获取request的相关信息，为后面的路由提供数据基础。
 */
public class BambooClientHttpRequestIntercptor implements ClientHttpRequestInterceptor {

    private BambooProperties bambooProperties;

    public BambooClientHttpRequestIntercptor(BambooProperties bambooProperties) {
        this.bambooProperties = bambooProperties;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        URI uri = request.getURI();
        BambooRequest.Builder bambooReqBuilder = BambooRequest.builder()
                .serviceId(uri.getHost())
                .uri(uri.getPath())
                .ip(RequestIpKeeper.getRequestIp())
                .addMultiHeaders(request.getHeaders())
                .addMultiParams(WebUtils.getQueryParams(uri.getQuery()));

        if (bambooProperties.getBambooRequest().isLoadBody()) {
            bambooReqBuilder.requestBody(body);
        }

        BambooRequest bambooRequest = bambooReqBuilder.build();

        ConnectPointContext connectPointContext = ConnectPointContext.builder().bambooRequest(bambooRequest).build();
        try {
            BambooAppContext.getBambooRibbonConnectionPoint().executeConnectPoint(connectPointContext);
            return execution.execute(request, body);
        } finally {
            BambooAppContext.getBambooRibbonConnectionPoint().shutdownconnectPoint();
        }
    }
}
