package cn.dingyuegroup.bamboo.gateway.filter;

import cn.dingyuegroup.bamboo.config.properties.BambooAppContext;
import cn.dingyuegroup.bamboo.config.properties.BambooProperties;
import cn.dingyuegroup.bamboo.context.BambooRequest;
import cn.dingyuegroup.bamboo.context.ConnectPointContext;
import cn.dingyuegroup.bamboo.utils.WebUtils;
import cn.dingyuegroup.bamboo.web.RequestIpKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by 170147 on 2019/3/8.
 */
public class BambooPreGatewayFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(BambooPreGatewayFilter.class);

    private BambooProperties bambooProperties;

    public BambooPreGatewayFilter(BambooProperties bambooProperties) {
        this.bambooProperties = bambooProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, GatewayFilterChain gatewayFilterChain) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        URI uri = request.getURI();
        BambooRequest.Builder bambooReqBuilder = BambooRequest.builder()
                .serviceId(uri.getHost())
                .uri(uri.getPath())
                .ip(RequestIpKeeper.getRequestIp())
                .addMultiHeaders(request.getHeaders())
                .addMultiParams(WebUtils.getQueryParams(uri.getQuery()));

        if (bambooProperties.getBambooRequest().isLoadBody()) {
            Flux<DataBuffer> body = serverWebExchange.getRequest().getBody();
            AtomicReference<byte[]> bodyRef = new AtomicReference<>();
            body.subscribe(buffer -> {
                byte[] bytes = new byte[buffer.readableByteCount()];
                buffer.read(bytes);
                //释放buffer资源，否则容易内存泄露
                DataBufferUtils.release(buffer);
                bodyRef.set(bytes);
            });
            bambooReqBuilder.requestBody(bodyRef.get());
        }

        BambooRequest bambooRequest = bambooReqBuilder.build();

        ConnectPointContext connectPointContext = ConnectPointContext.builder().bambooRequest(bambooRequest).build();

        BambooAppContext.getBambooRibbonConnectionPoint().executeConnectPoint(connectPointContext);
        return gatewayFilterChain.filter(serverWebExchange);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
