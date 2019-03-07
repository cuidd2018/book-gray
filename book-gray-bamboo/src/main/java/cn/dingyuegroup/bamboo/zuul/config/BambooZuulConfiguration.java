package cn.dingyuegroup.bamboo.zuul.config;

import cn.dingyuegroup.bamboo.config.properties.BambooProperties;
import cn.dingyuegroup.bamboo.zuul.filter.BambooPostZuulFilter;
import cn.dingyuegroup.bamboo.zuul.filter.BambooPreZuulFilter;
import com.netflix.zuul.http.ZuulServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@ConditionalOnClass(value = ZuulServlet.class)
public class BambooZuulConfiguration {
    @Autowired
    private BambooProperties bambooProperties;

    @Bean
    public BambooPreZuulFilter bambooPreZuulFilter() {
        return new BambooPreZuulFilter(bambooProperties);
    }

    @Bean
    public BambooPostZuulFilter bambooPostZuulFilter() {
        return new BambooPostZuulFilter();
    }
}
