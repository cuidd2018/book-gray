package cn.dingyuegroup.gray.server.context;

import cn.dingyuegroup.gray.core.InstanceLocalInfo;
import cn.dingyuegroup.gray.server.manager.GrayServiceManager;
import cn.dingyuegroup.gray.server.mysql.dao.GrayRbacUserMapper;
import cn.dingyuegroup.gray.server.service.GrayServerEvictor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PreDestroy;

public class GrayServerInitializingBean implements InitializingBean, ApplicationContextAware {
    private ApplicationContext cxt;

    @Override
    public void afterPropertiesSet() throws Exception {
        GrayServerContext.setGrayServiceManager(cxt.getBean(GrayServiceManager.class));
        GrayServerContext.setGrayServerEvictor(cxt.getBean(GrayServerEvictor.class));
        GrayServerContext.setGrayRbacUserMapper(cxt.getBean(GrayRbacUserMapper.class));
        GrayServerContext.setEnvironment(cxt.getEnvironment());
        GrayServerContext.setInstanceLocalInfo(cxt.getBean(InstanceLocalInfo.class));
        initToWork();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.cxt = applicationContext;
    }

    private void initToWork() {
        GrayServerContext.getGrayServiceManager().openForWork();
    }


    @PreDestroy
    public void shutdown() {
        GrayServerContext.getGrayServiceManager().shutdown();
    }
}
