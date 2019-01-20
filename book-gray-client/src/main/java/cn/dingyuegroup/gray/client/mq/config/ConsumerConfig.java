package cn.dingyuegroup.gray.client.mq.config;

import cn.dingyuegroup.gray.client.config.GrayMQMarkerConfiguration;
import cn.dingyuegroup.gray.client.mq.listener.SyncListener;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PropertyValueConst;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
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
@ConfigurationProperties(prefix = "mq.consumer")
public class ConsumerConfig {
    @Value("${mq.access_key}")
    private String accessKey;
    @Value("${mq.secret_key}")
    private String secretKey;
    @Value("${mq.ons_addr}")
    private String onsAddr;
    //ios同步账户消费者编号
    private String consumerId;
    //ios同步账户topic
    private String topic;
    //ios同步账户tag
    private String tag;

    @Autowired
    private SyncListener syncListener;

    @Bean(name = "syncConsumer", initMethod = "start", destroyMethod = "shutdown")
    public Consumer init() {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.ConsumerId, consumerId);
        properties.setProperty(PropertyKeyConst.AccessKey, accessKey);
        properties.setProperty(PropertyKeyConst.SecretKey, secretKey);
        properties.setProperty(PropertyKeyConst.ONSAddr, onsAddr);
        //设置每条消息消费的最大超时时间，超过设置时间则被视为消费失败，等下次重新投递再次消费。每个业务需要设置一个合理的值，单位（分钟）
        properties.setProperty(PropertyKeyConst.ConsumeTimeout, "1");
        //PropertyKeyConst#MaxCachedMessageSizeInMiB （范围在 16 MB ~ 2048 MB）默认最大消耗内存 512 MB（CID 订阅的所有 topic 缓存总和）
        // 集群订阅方式 (默认)
        // properties.setProperty(PropertyKeyConst.MessageModel, PropertyValueConst.CLUSTERING);
        // 广播订阅方式
        properties.setProperty(PropertyKeyConst.MessageModel, PropertyValueConst.BROADCASTING);
        Consumer consumer = ONSFactory.createConsumer(properties);
        consumer.subscribe(topic, tag, syncListener);
        consumer.start();
        return consumer;
    }
}
