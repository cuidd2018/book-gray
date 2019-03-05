package cn.dingyuegroup.gray.client.manager;

import cn.dingyuegroup.gray.client.decision.GrayDecision;
import cn.dingyuegroup.gray.client.decision.GrayDecisionFactory;
import cn.dingyuegroup.gray.client.decision.MultiGrayDecision;
import cn.dingyuegroup.gray.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;


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
    public boolean isOpenGray(String serviceId) {
        GrayService grayService = grayService(serviceId);
        return grayService != null && grayService.isOpenGray();
    }

    @Override
    public boolean isOnline(String serviceId, String instanceId) {
        GrayInstance grayInstance = grayInstance(serviceId, instanceId);
        if (grayInstance == null) {
            return true;//因为不拉取在线的服务，所以默认认为在线
        }
        return grayInstance.isStatus();
    }

    @Override
    public List<GrayService> listGrayService() {
        return client.listGrayService();
    }

    @Override
    public GrayService grayService(String serviceId) {
        List<GrayService> list = listGrayService();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Optional<GrayService> optional = list.parallelStream().filter(e -> e.getServiceId().equals(serviceId)).findAny();
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    @Override
    public GrayInstance grayInstance(String serviceId, String instanceId) {
        GrayService grayService = grayService(serviceId);
        if (grayService == null || CollectionUtils.isEmpty(grayService.getGrayInstances())) {
            return null;
        }
        Optional<GrayInstance> optional = grayService.getGrayInstances().parallelStream().filter(e -> e.getInstanceId().equals(instanceId)).findAny();
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
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
        log.info("\n----------------------------------------------------------\n\t"
                + "begin offline current instance\n\t----------------------------------------------------------");
        client.serviceDownline();
        log.info("\n----------------------------------------------------------\n\t"
                + "offline current instance done\n\t----------------------------------------------------------");
    }

    protected abstract void serviceShutdown();

    private GrayDecision toGrayDecision(GrayPolicyGroup policyGroup) {
        List<GrayPolicy> policies = policyGroup.getList();
        if (policies == null || policies.isEmpty()) {
            return GrayDecision.refuse();
        }
        if (policyGroup.getGroupType().equals(GrayPolicyGroup.TYPE.OR.name())) {
            MultiGrayDecision decision = new MultiGrayDecision(GrayDecision.refuse());
            policies.forEach(policy -> decision.or(decisionFactory.getDecision(policy)));
            return decision;
        } else {
            MultiGrayDecision decision = new MultiGrayDecision(GrayDecision.allow());
            policies.forEach(policy -> decision.and(decisionFactory.getDecision(policy)));
            return decision;
        }
    }

}
