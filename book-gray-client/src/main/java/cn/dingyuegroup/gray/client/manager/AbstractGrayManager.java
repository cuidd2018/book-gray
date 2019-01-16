package cn.dingyuegroup.gray.client.manager;

import cn.dingyuegroup.gray.client.decision.MultiGrayDecision;
import cn.dingyuegroup.gray.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * GrayManager的抽象实现类实现了基础的获取灰度列表， 创建灰度决策对象的能力
 */
public abstract class AbstractGrayManager implements GrayManager {

    private static final Logger log = LoggerFactory.getLogger(AbstractGrayManager.class);

    protected GrayDecisionFactory decisionFactory;
    protected InformationClient client;


    public AbstractGrayManager(InformationClient client, GrayDecisionFactory decisionFactory) {
        this.decisionFactory = decisionFactory;
        this.client = client;
    }


    @Override
    public boolean isOpen(String serviceId) {
        GrayService grayService = grayService(serviceId);
        return grayService != null
                && grayService.isOpenGray();
    }

    @Override
    public List<GrayService> listGrayService() {
        return client.listGrayService();
    }

    @Override
    public GrayService grayService(String serviceId) {
        return client.grayService(serviceId);
    }

    @Override
    public GrayInstance grayInstance(String serviceId, String instanceId) {
        return client.grayInstance(serviceId, instanceId);
    }

    @Override
    public GrayDecision grayDecision(String serviceId, String instanceId) {
        GrayInstance grayInstance = grayInstance(serviceId, instanceId);
        if (grayInstance == null || !grayInstance.isOpenGray()
                || grayInstance.getGrayPolicyGroup() == null) {
            return null;
        }
        GrayPolicyGroup grayPolicyGroup = grayInstance.getGrayPolicyGroup();
        if (!grayPolicyGroup.isEnable()) {
            return null;
        }
        GrayDecision grayDecision = toGrayDecision(grayPolicyGroup);
        if (grayDecision != GrayDecision.refuse()) {
            return grayDecision;
        }
        return null;
    }

    @Override
    public void serviceDownline() {
        log.debug("灰度服务下线...");
        client.serviceDownline();
        log.debug("灰度服务下线完成");
        serviceShutdown();
    }

    protected abstract void serviceShutdown();

    private GrayDecision toGrayDecision(GrayPolicyGroup policyGroup) {
        List<GrayPolicy> policies = policyGroup.getList();
        if (policies == null || policies.isEmpty()) {
            return GrayDecision.refuse();
        }
        MultiGrayDecision decision = new MultiGrayDecision(GrayDecision.allow());
        policies.forEach(policy -> decision.and(decisionFactory.getDecision(policy)));
        return decision;
    }

}
