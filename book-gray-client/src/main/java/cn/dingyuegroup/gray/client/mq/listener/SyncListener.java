package cn.dingyuegroup.gray.client.mq.listener;

import cn.dingyuegroup.gray.client.context.GrayClientAppContext;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author whx
 * ios用户同步账户使用
 */
@Component
public class SyncListener implements MessageListener {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        try {
            byte[] body = message.getBody();
            String json = new String(body);
            logger.info("MQ广播拉新服务列表：{}", json);
            GrayClientAppContext.getGrayManager().updateCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Action.CommitMessage;
    }
}
