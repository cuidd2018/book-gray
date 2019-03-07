package cn.dingyuegroup.gray.client.config;

import cn.dingyuegroup.gray.client.config.properties.GrayClientAppContext;
import cn.dingyuegroup.gray.client.config.properties.GrayClientProperties;
import cn.dingyuegroup.gray.client.manager.GrayManager;
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
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.cxt = applicationContext;
    }

    @PreDestroy
    public void shutdown() {
        GrayClientAppContext.getGrayManager().serviceDownline();
    }

    private void startForWork() {
        GrayClientAppContext.getGrayManager().openForWork();
    }
}