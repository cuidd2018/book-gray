package cn.dingyuegroup.bamboo.feign;

import cn.dingyuegroup.bamboo.config.properties.BambooAppContext;
import cn.dingyuegroup.bamboo.context.BambooRequest;
import cn.dingyuegroup.bamboo.context.ConnectPointContext;
import cn.dingyuegroup.bamboo.config.properties.BambooProperties;
import cn.dingyuegroup.bamboo.utils.WebUtils;
import cn.dingyuegroup.bamboo.context.RequestIpKeeper;
import feign.Client;
import feign.Request;
import feign.Response;

import java.io.IOException;
import java.net.URI;

/**
 * 主要作用是用来获取request的相关信息，为后面的路由提供数据基础。
 */
public class BambooFeignClient implements Client {

    private Client delegate;
    private BambooProperties bambooProperties;

    public BambooFeignClient(BambooProperties bambooProperties, Client delegate) {
        this.delegate = delegate;
        this.bambooProperties = bambooProperties;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        URI uri = URI.create(request.url());
        BambooRequest.Builder builder = BambooRequest.builder()
                .serviceId(uri.getHost())
                .uri(uri.getPath())
                .ip(RequestIpKeeper.getRequestIp())
                .addMultiParams(WebUtils.getQueryParams(uri.getQuery()));
        if (bambooProperties.getBambooRequest().isLoadBody()) {
            builder.requestBody(request.body());
        }

        request.headers().entrySet().forEach(entry -> {
            for (String v : entry.getValue()) {
                builder.addHeader(entry.getKey(), v);
            }
        });

        ConnectPointContext connectPointContext = ConnectPointContext.builder().bambooRequest(builder.build()).build();

        try {
            BambooAppContext.getBambooRibbonConnectionPoint().executeConnectPoint(connectPointContext);
            return delegate.execute(request, options);
        } finally {
            BambooAppContext.getBambooRibbonConnectionPoint().shutdownconnectPoint();
        }
    }
}
