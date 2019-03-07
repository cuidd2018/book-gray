package cn.dingyuegroup.gray.client.ribbon;

import cn.dingyuegroup.bamboo.config.properties.BambooAppContext;
import cn.dingyuegroup.bamboo.context.BambooRequest;
import cn.dingyuegroup.bamboo.context.BambooRequestContext;
import cn.dingyuegroup.gray.client.utils.ServiceUtil;
import cn.dingyuegroup.gray.client.decision.GrayDecision;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.loadbalancer.Server;

import java.util.Map;

public class GrayDecisionPredicate extends AbstractServerPredicate {

    public GrayDecisionPredicate(GrayLoadBalanceRule rule) {
        super(rule);
    }

    @Override
    public boolean apply(PredicateKey input) {
        BambooRequestContext bambooRequestContext = BambooRequestContext.currentRequestContext();
        if (bambooRequestContext == null || bambooRequestContext.getBambooRequest() == null) {
            return false;
        }
        BambooRequest bambooRequest = bambooRequestContext.getBambooRequest();
        Server server = input.getServer();
        String serviceId = bambooRequest.getServiceId();
        Map<String, String> serverMetadata = getServerMetadata(serviceId, server);
        String instanceId = ServiceUtil.getInstanceId(server, serverMetadata);
        GrayDecision grayDecision = getIRule().getGrayManager().grayDecision(serviceId, instanceId);
        if (grayDecision.test(bambooRequest)) {
            return true;
        }
        return false;
    }


    protected GrayLoadBalanceRule getIRule() {
        return (GrayLoadBalanceRule) this.rule;
    }

    public Map<String, String> getServerMetadata(String serviceId, Server server) {
        return BambooAppContext.getEurekaServerExtractor().getServerMetadata(serviceId, server);
    }
}
