package cn.dingyuegroup.gray.server.vertify;

import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayService;
import cn.dingyuegroup.gray.server.manager.GrayServiceManager2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by whx on 2017/5/11.
 * 其中spring.aop.auto属性默认是开启的，也就是说只要引入了AOP依赖后，默认已经增加了@EnableAspectJAutoProxy。
 * 而当我们需要使用CGLIB来实现AOP的时候，需要配置spring.aop.proxy-target-class=true，不然默认使用的是标准Java的实现。
 */
@Order(1)
@Component
@Aspect
public class VertifyRequestAop {

    Logger logger = LoggerFactory.getLogger(VertifyRequestAop.class);

    @Autowired
    private GrayServiceManager2 grayServiceManager2;

    //定义切面：只有controller包下的接口才进行vertifyRequest校验
    @Pointcut("execution(public * cn.dingyuegroup.gray.server.web..*.*(..)) and @annotation(org.springframework.web.bind.annotation.*)")
    public void executeController() {

    }

    //定义切入点
    @Pointcut("@annotation(cn.dingyuegroup.gray.server.vertify.VertifyRequest)")
    public void vertifyRequest() {

    }

    /**
     * "@annotation(vertifyRequest)"里的参数要和方法参数一致
     *
     * @param proceedingJoinPoint
     * @param vertifyRequest
     * @return
     * @throws Throwable
     */
    @Around("vertifyRequest() and @annotation(vertifyRequest)")
    public Object doAroundAdvice(ProceedingJoinPoint proceedingJoinPoint, VertifyRequest vertifyRequest) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        boolean flag = verify(request);
        if (!flag) {
            return ResponseEntity.badRequest();
        }
        // result的值就是被拦截方法的返回值
        Object result = proceedingJoinPoint.proceed();
        return result;
    }


    /**
     * 封装错误码
     *
     * @param request
     * @return
     */
    private boolean verify(HttpServletRequest request) {
        String serviceId = request.getParameter("serviceId");
        String instanceId = request.getParameter("instanceId");
        boolean b = true;
        if (!StringUtils.isEmpty(serviceId)) {
            b = b && vertifyService(serviceId);
            if (!StringUtils.isEmpty(instanceId)) {
                b = b && vertifyInstance(serviceId, instanceId);
            }
        }
        return b;
    }

    public boolean vertifyService(String serviceId) {
        GrayService grayService = grayServiceManager2.getGrayService(serviceId);
        if (grayService != null) {
            return true;
        }
        return false;
    }

    /**
     * 校验
     *
     * @param serviceId
     * @param instanceId
     * @return
     */
    public boolean vertifyInstance(String serviceId, String instanceId) {
        GrayInstance grayInstance = grayServiceManager2.getGrayInstance(serviceId, instanceId);
        if (grayInstance != null) {
            return true;
        }
        return false;
    }
}
