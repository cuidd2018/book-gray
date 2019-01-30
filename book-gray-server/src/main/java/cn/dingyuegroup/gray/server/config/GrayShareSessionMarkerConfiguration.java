package cn.dingyuegroup.gray.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrayShareSessionMarkerConfiguration {

    @Bean
    public GrayShareSessionMarker grayShareSessionMarkerBean() {
        return new GrayShareSessionMarker();
    }

    public class GrayShareSessionMarker {
    }
}
