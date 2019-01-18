package cn.dingyuegroup.gray.server.config;

import cn.dingyuegroup.gray.server.GrayServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {GrayServer.class})
public class GrayServerMarkerConfiguration {

    @Bean
    public GrayServerMarker grayServerMarkerBean() {
        return new GrayServerMarker();
    }

    class GrayServerMarker {
    }
}
