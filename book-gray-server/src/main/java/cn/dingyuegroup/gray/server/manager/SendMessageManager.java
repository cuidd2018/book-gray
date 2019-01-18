package cn.dingyuegroup.gray.server.manager;

/**
 * Created by 170147 on 2019/1/18.
 */
public interface SendMessageManager {

    /**
     * 发送MQ消息
     *
     * @param json
     */
    void sendMessage(String json);
}
