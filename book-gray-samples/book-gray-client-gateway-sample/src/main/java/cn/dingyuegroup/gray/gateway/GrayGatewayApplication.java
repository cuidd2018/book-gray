package cn.dingyuegroup.gray.gateway;

import cn.dingyuegroup.gray.client.EnableGrayClient;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringCloudApplication
@EnableDiscoveryClient
@EnableGrayClient
public class GrayGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrayGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/api/**")
                        .uri("lb://SERVICE-A")
                        .id("auth")
                )
                .build();
    }
}
