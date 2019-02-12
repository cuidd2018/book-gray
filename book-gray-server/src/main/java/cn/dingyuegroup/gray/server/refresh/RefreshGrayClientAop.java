package cn.dingyuegroup.gray.server.refresh;

import cn.dingyuegroup.gray.server.config.GrayMQMarkerConfiguration;
import cn.dingyuegroup.gray.server.manager.SendMessageManager;
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

/**
 * Created by whx on 2017/5/11.
 * 其中spring.aop.auto属性默认是开启的，也就是说只要引入了AOP依赖后，默认已经增加了@EnableAspectJAutoProxy。
 * 而当我们需要使用CGLIB来实现AOP的时候，需要配置spring.aop.proxy-target-class=true，不然默认使用的是标准Java的实现。
 */
@Component
@Aspect
@ConditionalOnBean(GrayMQMarkerConfiguration.GrayMQMarker.class)
public class RefreshGrayClientAop {

    Logger logger = LoggerFactory.getLogger(RefreshGrayClientAop.class);

    @Autowired
    private SendMessageManager sendMessageManager;

    //定义切入点
    @Pointcut("@annotation(cn.dingyuegroup.gray.server.refresh.RefreshGrayClient)")
    public void refreshGrayClient() {

    }

    /**
     * "@annotation(vertifyRequest)"里的参数要和方法参数一致
     *
     * @return
     * @throws Throwable
     */
    @After("refreshGrayClient()")
    public void doAroundAdvice(JoinPoint joinPoint) throws Throwable {
        sendMessageManager.sendMessage(JSON.toJSONString(new HashMap<>()));
    }
}
