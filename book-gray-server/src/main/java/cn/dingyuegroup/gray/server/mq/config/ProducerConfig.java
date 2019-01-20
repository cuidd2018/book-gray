package cn.dingyuegroup.gray.server.mq.config;

import cn.dingyuegroup.gray.server.config.GrayMQMarkerConfiguration;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PropertyValueConst;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Properties;

/**
 * @author whx
 * @date 2018/9/27 0027
 */

@Data
@Configuration
@ConditionalOnBean(GrayMQMarkerConfiguration.GrayMQMarker.class)
@PropertySource(value = "classpath:${gray.mq.path}", ignoreResourceNotFound = true)
@ConfigurationProperties(prefix = "mq.producer")
public class ProducerConfig {
    @Value("${mq.access_key}")
    private String accessKey;
    @Value("${mq.secret_key}")
    private String secretKey;
    @Value("${mq.ons_addr}")
    private String onsAddr;
    private String producerId;
    //设置发送超时时间，单位毫秒
    private String sendMsgTimeoutMillis;

    @Bean(name = "producer", initMethod = "start", destroyMethod = "shutdown")
    public Producer init() {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.ProducerId, producerId);
        properties.setProperty(PropertyKeyConst.AccessKey, accessKey);
        properties.setProperty(PropertyKeyConst.SecretKey, secretKey);
        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, sendMsgTimeoutMillis);
        properties.setProperty(PropertyKeyConst.ONSAddr, onsAddr);
        properties.setProperty(PropertyKeyConst.MessageModel, PropertyValueConst.BROADCASTING);

        Producer producer = ONSFactory.createProducer(properties);
        // 在发送消息前，必须调用 start 方法来启动 Producer，只需调用一次即可
        producer.start();
        return producer;
    }
}
