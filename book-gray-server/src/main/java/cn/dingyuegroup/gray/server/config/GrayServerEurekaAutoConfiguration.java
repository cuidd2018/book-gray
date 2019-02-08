package cn.dingyuegroup.gray.server.config;

import cn.dingyuegroup.gray.core.InstanceLocalInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(EurekaClient.class)
@AutoConfigureAfter(EurekaClientAutoConfiguration.class)
public class GrayServerEurekaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public InstanceLocalInfo instanceLocalInfo(@Autowired EurekaRegistration registration) {
        String instanceId = registration.getInstanceConfig().getInstanceId();
        InstanceLocalInfo localInfo = new InstanceLocalInfo();
        localInfo.setInstanceId(instanceId);
        localInfo.setServiceId(registration.getServiceId());
        return localInfo;
    }
}
