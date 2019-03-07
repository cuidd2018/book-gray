package cn.dingyuegroup.gray.server.refresh;

import cn.dingyuegroup.gray.server.config.GrayMQMarkerConfiguration;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Aspect
@ConditionalOnBean(GrayMQMarkerConfiguration.GrayMQMarker.class)
public class RefreshGrayClientAop {

    Logger logger = LoggerFactory.getLogger(RefreshGrayClientAop.class);

    @Autowired
    private SyncMessageService syncMessageService;

    //定义切入点
    @Pointcut("@annotation(cn.dingyuegroup.gray.server.refresh.RefreshGrayClient)")
    public void refreshGrayClient() {

    }

    /**
     * @throws Throwable
     */
    @After("refreshGrayClient()")
    public void doAroundAdvice(JoinPoint joinPoint) throws Throwable {
        syncMessageService.sendMessage(JSON.toJSONString(new HashMap<>()));
    }
}
