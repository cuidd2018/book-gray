package cn.dingyuegroup.gray.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrayMQMarkerConfiguration {

    @Bean
    public GrayMQMarker grayMQMarkerBean() {
        return new GrayMQMarker();
    }

    public class GrayMQMarker {
    }
}
