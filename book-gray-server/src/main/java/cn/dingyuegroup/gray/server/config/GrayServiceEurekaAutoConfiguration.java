package cn.dingyuegroup.gray.server.config;

import cn.dingyuegroup.gray.server.manager.GrayServiceManager;
import cn.dingyuegroup.gray.server.mysql.dao.GrayServiceMapper;
import cn.dingyuegroup.gray.server.service.AbstractGrayService;
import cn.dingyuegroup.gray.server.service.GrayServerEvictor;
import cn.dingyuegroup.gray.server.service.impl.EurekaGrayServerEvictor;
import cn.dingyuegroup.gray.server.service.impl.EurekaGrayService;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean({EurekaClient.class})
public class GrayServiceEurekaAutoConfiguration {

    @Autowired
    private EurekaClient eurekaClient;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private GrayServiceManager grayServiceManager;
    @Autowired
    private GrayServiceMapper grayServiceMapper;

    @Bean
    public AbstractGrayService grayService() {
        return new EurekaGrayService(eurekaClient, discoveryClient, grayServiceManager, grayServiceMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public GrayServerEvictor grayServerEvictor() {
        return new EurekaGrayServerEvictor(eurekaClient);
    }
}
