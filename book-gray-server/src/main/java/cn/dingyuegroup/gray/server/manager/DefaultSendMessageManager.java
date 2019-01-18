package cn.dingyuegroup.gray.server.manager;

import cn.dingyuegroup.gray.server.config.GrayMQMarkerConfiguration;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 170147 on 2019/1/18.
 */
@Service
@ConditionalOnBean(GrayMQMarkerConfiguration.GrayMQMarker.class)
public class DefaultSendMessageManager implements SendMessageManager {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${mq.consumer.topic}")
    private String topic;//发送消息主题
    @Value("${mq.consumer.tag}")
    private String tag;
    @Autowired
    private Producer producer;

    @Override
    public void sendMessage(String json) {
        Message msg = new Message( //
                // Message 所属的 Topic
                topic,
                // Message Tag 可理解为 Gmail 中的标签，对消息进行再归类，方便 Consumer 指定过滤条件在 MQ 服务器过滤
                tag,
                // Message Body 可以是任何二进制形式的数据， MQ 不做任何干预，
                // 需要 Producer 与 Consumer 协商好一致的序列化和反序列化方式
                json.getBytes());
        // 设置代表消息的业务关键属性，请尽可能全局唯一。
        // 以方便您在无法正常收到消息情况下，可通过阿里云服务器管理控制台查询消息并补发
        // 注意：不设置也不会影响消息正常收发
        msg.setKey("MSG_KEY_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        try {
            producer.sendOneway(msg);
        } catch (Exception e) {
            logger.error(" Send mq message failed. Topic is:{}" + msg.getTopic(), e);
        }
    }
}
