package cn.dingyuegroup.bamboo.autoconfig;

import cn.dingyuegroup.bamboo.*;
import cn.dingyuegroup.bamboo.autoconfig.properties.BambooProperties;
import cn.dingyuegroup.bamboo.feign.config.BambooFeignConfiguration;
import cn.dingyuegroup.bamboo.ribbon.BambooClientHttpRequestIntercptor;
import cn.dingyuegroup.bamboo.ribbon.EurekaServerExtractor;
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
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;


@Configuration
@EnableConfigurationProperties({BambooProperties.class})
@AutoConfigureBefore({BambooFeignConfiguration.class, BambooZuulConfiguration.class})
@Import(BambooWebConfiguration.class)
//@RibbonClients(defaultConfiguration = {BambooExtConfigration.class})
@RibbonClients(defaultConfiguration = BambooRibbonClientsConfiguration.class)
public class BambooAutoConfiguration {

    public static class UnUseBambooIRule {

    }


//    @Autowired(required = false)
//    private IClientConfig config;

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


//    @Bean
//    @ConditionalOnMissingBean(value = {BambooAutoConfiguration.UnUseBambooIRule.class})
//    public IRule ribbonRule() {
//        BambooZoneAvoidanceRule rule = new BambooZoneAvoidanceRule();
//        rule.initWithNiwsConfig(config);
//        return rule;
//    }

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
