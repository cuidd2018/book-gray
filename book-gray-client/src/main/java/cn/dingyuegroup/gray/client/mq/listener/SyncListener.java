package cn.dingyuegroup.gray.client.mq.listener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.springframework.stereotype.Component;

/**
 * @author whx
 * @date 2018/9/27 0027
 * ios用户同步账户使用
 */
@Component
public class SyncListener implements MessageListener {

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        try {
            byte[] body = message.getBody();
            String json = new String(body);
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Action.CommitMessage;
    }
}
