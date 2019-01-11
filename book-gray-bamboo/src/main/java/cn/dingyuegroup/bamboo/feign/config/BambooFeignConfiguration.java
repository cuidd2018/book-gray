package cn.dingyuegroup.bamboo.feign.config;

import com.netflix.loadbalancer.ILoadBalancer;
import feign.Feign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@ConditionalOnClass(value = {ILoadBalancer.class, Feign.class})
@Configuration
@EnableFeignClients(defaultConfiguration = {BambooFeignClientsConfiguration.class})
public class BambooFeignConfiguration {

}
