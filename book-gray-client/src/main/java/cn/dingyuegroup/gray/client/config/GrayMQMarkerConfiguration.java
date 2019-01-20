package cn.dingyuegroup.gray.client.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrayMQMarkerConfiguration {

    @Bean
    public GrayMQMarker grayMQMarker() {
        return new GrayMQMarker();
    }

    public class GrayMQMarker {
    }
}
