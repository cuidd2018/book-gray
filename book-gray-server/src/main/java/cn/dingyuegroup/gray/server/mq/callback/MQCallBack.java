package cn.dingyuegroup.gray.server.mq.callback;

import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author whx
 */
@Component
public class MQCallBack implements SendCallback {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onSuccess(SendResult sendResult) {
        // 消费发送成功
        logger.info("send message success. topic=" + sendResult.getTopic() + ", msgId=" + sendResult.getMessageId());
    }

    @Override
    public void onException(OnExceptionContext onExceptionContext) {
        // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理
        logger.error("send message failed. topic=" + onExceptionContext.getTopic() + ", msgId=" + onExceptionContext.getMessageId());
    }
}
