package cn.dingyuegroup.gray.server.vertify;

import cn.dingyuegroup.gray.server.service.AbstractGrayService;
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
    private AbstractGrayService grayService;

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
            b = b && grayService.vertifyService(serviceId);
            if (!StringUtils.isEmpty(instanceId)) {
                b = b && grayService.vertifyInstance(serviceId, instanceId);
            }
        }
        return b;
    }
}
