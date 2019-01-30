package cn.dingyuegroup.gray.server.redis;

import cn.dingyuegroup.gray.server.config.GrayShareSessionMarkerConfiguration;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author whx
 * @date 2017/9/29 0029
 */
@Data
@Configuration
@ConditionalOnBean(GrayShareSessionMarkerConfiguration.GrayShareSessionMarker.class)
@PropertySource(value = "classpath:${gray.redis.path}", ignoreResourceNotFound = true)
@ConfigurationProperties(prefix = "redis.session")
public class JedisConfig {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private String hostName;
    private int port;
    private String password;
    private int database;

    private int maxWaitMillis;
    private int minEvictableIdleTimeMillis;
    private int timeBetweenEvictionRunsMillis;
    private int timeout;
    private boolean usePool;
    private int maxTotal;
    private int maxIdle;
    private int minIdle;

    @Bean(name = "jedisPoolConfig")
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        return jedisPoolConfig;
    }

    @Bean(name = "jedisConnectionFactory", destroyMethod = "destroy")
    public JedisConnectionFactory jedisConnectionFactory(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig) {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setPoolConfig(jedisPoolConfig);
        factory.setHostName(hostName);
        factory.setPort(port);
        factory.setPassword(password);
        factory.setTimeout(timeout);
        factory.setUsePool(usePool);
        factory.setDatabase(database);
        return factory;
    }

    @Bean(value = "stringRedisTemplateVip")
    public StringRedisTemplate stringRedisTemplate(@Qualifier("jedisConnectionFactory") JedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);
        return template;
    }
}
