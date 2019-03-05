package cn.dingyuegroup.gray.client.context;

import cn.dingyuegroup.gray.client.config.properties.GrayClientProperties;
import cn.dingyuegroup.gray.core.GrayManager;
import cn.dingyuegroup.gray.core.InstanceLocalInfo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PreDestroy;

public class GrayClientInitializingBean implements InitializingBean, ApplicationContextAware {
    private ApplicationContext cxt;

    @Override
    public void afterPropertiesSet() throws Exception {
        GrayClientAppContext.setGrayManager(cxt.getBean(GrayManager.class));
        GrayClientAppContext.setInstanceLocalInfo(cxt.getBean(InstanceLocalInfo.class));
        GrayClientAppContext.setGrayClientProperties(cxt.getBean(GrayClientProperties.class));
        GrayClientAppContext.setEnvironment(cxt.getEnvironment());
        startForWork();

//        registrShutdownFunc();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.cxt = applicationContext;
    }

//    private void registrShutdownFunc(){
//        Runtime.getRuntime().addShutdownHook(new Thread(()->{
//            shutdown();
//        }));
//    }

    @PreDestroy
    public void shutdown() {
        GrayClientAppContext.getGrayManager().serviceDownline();
    }

    private void startForWork() {
        GrayClientAppContext.getGrayManager().openForWork();
    }
}