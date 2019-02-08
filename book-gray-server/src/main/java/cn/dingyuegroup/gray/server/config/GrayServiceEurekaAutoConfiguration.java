package cn.dingyuegroup.gray.server.config;

import cn.dingyuegroup.gray.server.manager.GrayServiceManager;
import cn.dingyuegroup.gray.server.mysql.dao.GrayInstanceMapper;
import cn.dingyuegroup.gray.server.mysql.dao.GrayRbacResourcesMapper;
import cn.dingyuegroup.gray.server.mysql.dao.GrayServiceMapper;
import cn.dingyuegroup.gray.server.service.AbstractGrayService;
import cn.dingyuegroup.gray.server.service.GrayServerEvictor;
import cn.dingyuegroup.gray.server.service.impl.EurekaGrayServerEvictor;
import cn.dingyuegroup.gray.server.service.impl.EurekaGrayService;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(EurekaClientAutoConfiguration.class)
@ConditionalOnBean({EurekaClient.class})
@SuppressWarnings("SpringJavaAutowiringInspection")
public class GrayServiceEurekaAutoConfiguration {

    @Autowired
    private EurekaClient eurekaClient;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private GrayServiceMapper grayServiceMapper;
    @Autowired
    private GrayInstanceMapper grayInstanceMapper;
    @Autowired
    private GrayServiceManager grayServiceManager;
    @Autowired
    private GrayRbacResourcesMapper grayRbacResourcesMapper;

    @Bean
    public AbstractGrayService grayService() {
        return new EurekaGrayService(eurekaClient, discoveryClient, grayServiceMapper, grayInstanceMapper, grayServiceManager, grayRbacResourcesMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public GrayServerEvictor grayServerEvictor() {
        return new EurekaGrayServerEvictor(eurekaClient);
    }
}
