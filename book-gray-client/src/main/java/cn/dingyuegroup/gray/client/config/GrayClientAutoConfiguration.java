package cn.dingyuegroup.gray.client.config;

import cn.dingyuegroup.bamboo.BambooConstants;
import cn.dingyuegroup.bamboo.autoconfig.BambooAutoConfiguration;
import cn.dingyuegroup.gray.client.config.properties.GrayClientProperties;
import cn.dingyuegroup.gray.client.config.properties.GrayOptionalArgs;
import cn.dingyuegroup.gray.client.context.GrayClientInitializingBean;
import cn.dingyuegroup.gray.client.decision.DefaultGrayDecisionFactory;
import cn.dingyuegroup.gray.client.manager.DefaultGrayManager;
import cn.dingyuegroup.gray.client.manager.HttpInformationClient;
import cn.dingyuegroup.gray.client.manager.RetryableInformationClient;
import cn.dingyuegroup.gray.core.GrayDecisionFactory;
import cn.dingyuegroup.gray.core.GrayManager;
import cn.dingyuegroup.gray.core.InformationClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@EnableConfigurationProperties({GrayClientProperties.class})
@ConditionalOnBean(GrayClientMarkerConfiguration.GrayClientMarker.class)
@RibbonClients(defaultConfiguration = GrayRibbonClientsConfiguration.class)
public class GrayClientAutoConfiguration {


    @Bean
    public BambooAutoConfiguration.UnUseBambooIRule unUseBambooIRule() {
        return new BambooAutoConfiguration.UnUseBambooIRule();
    }

    @Bean
    @Order(value = BambooConstants.INITIALIZING_ORDER + 1)
    public GrayClientInitializingBean grayClientInitializingBean() {
        return new GrayClientInitializingBean();
    }

    @Bean
    @ConditionalOnMissingBean
    public GrayDecisionFactory grayDecisionFactory() {
        return new DefaultGrayDecisionFactory();
    }


    @Configuration
    @ConditionalOnProperty(prefix = "gray.client", value = "information-client", havingValue = "http", matchIfMissing = true)
    public static class HttpGrayManagerClientConfiguration {
        @Autowired
        private GrayClientProperties grayClientProperties;

        @Bean
        public InformationClient informationClient(@Autowired RestTemplate restTemplate) {
            InformationClient client = new HttpInformationClient(grayClientProperties.getServerUrl(), restTemplate);
            if (true) {
                return client;
            }
            return new RetryableInformationClient(grayClientProperties.getRetryNumberOfRetries(), client);
        }


        @Bean
        public GrayManager grayManager(InformationClient informationClient, GrayDecisionFactory grayDecisionFactory) {
            GrayOptionalArgs args = new GrayOptionalArgs();
            args.setDecisionFactory(grayDecisionFactory);
            args.setGrayClientConfig(grayClientProperties);
            args.setInformationClient(informationClient);
            return new DefaultGrayManager(args);
        }
    }


}
