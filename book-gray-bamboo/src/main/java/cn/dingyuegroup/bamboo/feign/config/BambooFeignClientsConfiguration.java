package cn.dingyuegroup.bamboo.feign.config;

import cn.dingyuegroup.bamboo.config.properties.BambooProperties;
import cn.dingyuegroup.bamboo.feign.BambooFeignClient;
import feign.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
public class BambooFeignClientsConfiguration {


    @Autowired
    private Client feignClient;
    @Autowired
    private BambooProperties bambooProperties;

    @Bean
    public Client bambooFeignClient() {
        return new BambooFeignClient(bambooProperties, feignClient);
    }

}
