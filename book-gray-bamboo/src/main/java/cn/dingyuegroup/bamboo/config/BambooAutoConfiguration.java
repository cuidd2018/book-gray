package cn.dingyuegroup.bamboo.config;

import cn.dingyuegroup.bamboo.BambooRibbonConnectionPoint;
import cn.dingyuegroup.bamboo.DefaultRibbonConnectionPoint;
import cn.dingyuegroup.bamboo.LoadBalanceRequestTrigger;
import cn.dingyuegroup.bamboo.RequestVersionExtractor;
import cn.dingyuegroup.bamboo.config.properties.BambooConstants;
import cn.dingyuegroup.bamboo.config.properties.BambooProperties;
import cn.dingyuegroup.bamboo.feign.config.BambooFeignConfiguration;
import cn.dingyuegroup.bamboo.gateway.config.BambooGatewayConfiguration;
import cn.dingyuegroup.bamboo.ribbon.BambooClientHttpRequestIntercptor;
import cn.dingyuegroup.bamboo.ribbon.EurekaServerExtractor;
import cn.dingyuegroup.bamboo.ribbon.config.BambooRibbonClientsConfiguration;
import cn.dingyuegroup.bamboo.zuul.config.BambooZuulConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@EnableConfigurationProperties({BambooProperties.class})
@AutoConfigureBefore({BambooFeignConfiguration.class, BambooZuulConfiguration.class, BambooGatewayConfiguration.class})
@RibbonClients(defaultConfiguration = BambooRibbonClientsConfiguration.class)
public class BambooAutoConfiguration {

    public static class UnUseBambooIRule {

    }

    @Autowired
    private SpringClientFactory springClientFactory;
    @Autowired
    private BambooProperties bambooProperties;

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BambooClientHttpRequestIntercptor(bambooProperties));
        return restTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public EurekaServerExtractor eurekaServerExtractor() {
        return new EurekaServerExtractor(springClientFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestVersionExtractor requestVersionExtractor() {
        return new RequestVersionExtractor.Default();
    }

    @Bean
    @ConditionalOnMissingBean
    public BambooRibbonConnectionPoint bambooRibbonConnectionPoint(
            RequestVersionExtractor requestVersionExtractor,
            @Autowired(required = false) List<LoadBalanceRequestTrigger> requestTriggerList) {
        if (requestTriggerList != null) {
            requestTriggerList = Collections.EMPTY_LIST;
        }
        return new DefaultRibbonConnectionPoint(requestVersionExtractor, requestTriggerList);
    }

    @Bean
    @Order(value = BambooConstants.INITIALIZING_ORDER)
    public BambooInitializingBean bambooInitializingBean() {
        return new BambooInitializingBean();
    }

}
