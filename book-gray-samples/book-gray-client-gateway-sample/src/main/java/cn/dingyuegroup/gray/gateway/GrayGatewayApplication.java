package cn.dingyuegroup.gray.gateway;

import cn.dingyuegroup.gray.client.EnableGrayClient;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringCloudApplication
@EnableDiscoveryClient
@EnableGrayClient
public class GrayGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrayGatewayApplication.class, args);
    }
}
