package cn.dingyuegroup.bamboo.gateway.config;

import cn.dingyuegroup.bamboo.config.properties.BambooProperties;
import cn.dingyuegroup.bamboo.gateway.filter.BambooPostGatewayFilter;
import cn.dingyuegroup.bamboo.gateway.filter.BambooPreGatewayFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@ConditionalOnBean(RouteLocator.class)
public class BambooGatewayConfiguration {
    @Autowired
    private BambooProperties bambooProperties;

    @Bean
    public BambooPreGatewayFilter bambooPreGatewayFilter() {
        return new BambooPreGatewayFilter(bambooProperties);
    }

    @Bean
    public BambooPostGatewayFilter bambooPostGatewayFilter() {
        return new BambooPostGatewayFilter();
    }
}
