package cn.dingyuegroup.gray.client.config;

import cn.dingyuegroup.bamboo.config.properties.BambooConstants;
import cn.dingyuegroup.bamboo.config.BambooAutoConfiguration;
import cn.dingyuegroup.gray.client.config.properties.GrayClientProperties;
import cn.dingyuegroup.gray.client.config.properties.GrayOptionalArgs;
import cn.dingyuegroup.gray.client.decision.DefaultGrayDecisionFactory;
import cn.dingyuegroup.gray.client.decision.GrayDecisionFactory;
import cn.dingyuegroup.gray.client.manager.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

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
    @ConditionalOnProperty(prefix = "gray.client", value = {"server-name"})
    public static class HttpGrayManagerClientConfiguration {
        @Autowired
        private GrayClientProperties grayClientProperties;

        @Autowired
        private DefaultGrayServiceApi defaultGrayServiceApi;

        @Bean
        public InformationClient informationClient() {
            return new HttpInformationClient(grayClientProperties.getServerName(), defaultGrayServiceApi);
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
