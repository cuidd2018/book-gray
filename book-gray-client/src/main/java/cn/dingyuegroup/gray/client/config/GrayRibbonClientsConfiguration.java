package cn.dingyuegroup.gray.client.config;

import cn.dingyuegroup.gray.client.ribbon.GrayLoadBalanceRule;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
public class GrayRibbonClientsConfiguration {

    /**
     * 项目启动的时候不会初始化，有ribbon请求的时候才初始化
     *
     * @param config
     * @return
     */
    @Bean
    public IRule ribbonRule(@Autowired(required = false) IClientConfig config) {
        GrayLoadBalanceRule rule = new GrayLoadBalanceRule();
        rule.initWithNiwsConfig(config);
        return rule;
    }
}
