package cn.dingyuegroup.gray.server.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Created by 170147 on 2019/1/30.
 */
@Configuration
@ConditionalOnBean(GrayShareSessionMarkerConfiguration.GrayShareSessionMarker.class)
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 36000, redisNamespace = "book:gray:session")
public class RedisSessionConfiguration {
}
