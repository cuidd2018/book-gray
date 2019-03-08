package cn.dingyuegroup.bamboo.gateway.filter;

import cn.dingyuegroup.bamboo.utils.WebUtils;
import cn.dingyuegroup.bamboo.context.RequestIpKeeper;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Created by 170147 on 2019/3/8.
 */
public class BambooGatewayIpKeepFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, GatewayFilterChain gatewayFilterChain) {
        //获取ip
        String ip = WebUtils.getGatewayIpAddr(serverWebExchange.getRequest());
        //保存
        RequestIpKeeper.instance().setIp(ip);
        return gatewayFilterChain.filter(serverWebExchange).then(
                Mono.fromRunnable(() -> {
                    //清除ThreadLocal
                    RequestIpKeeper.instance().clear();
                }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE + 1;
    }
}
