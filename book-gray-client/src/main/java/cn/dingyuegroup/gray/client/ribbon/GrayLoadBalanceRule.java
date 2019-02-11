package cn.dingyuegroup.gray.client.ribbon;

import cn.dingyuegroup.bamboo.BambooAppContext;
import cn.dingyuegroup.bamboo.BambooRequestContext;
import cn.dingyuegroup.bamboo.ribbon.loadbalancer.BambooZoneAvoidanceRule;
import cn.dingyuegroup.gray.client.config.properties.GrayClientProperties;
import cn.dingyuegroup.gray.client.context.GrayClientAppContext;
import cn.dingyuegroup.gray.client.utils.ServiceUtil;
import cn.dingyuegroup.gray.core.GrayManager;
import cn.dingyuegroup.gray.core.GrayService;
import com.google.common.base.Optional;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 灰度发布的负载规则
 */
public class GrayLoadBalanceRule extends ZoneAvoidanceRule {

    protected CompositePredicate grayCompositePredicate;
    protected ZoneAvoidanceRule subDelegate;

    public GrayLoadBalanceRule() {
        super();
        init();
    }

    protected void init() {
        GrayDecisionPredicate apiVersionPredicate = new GrayDecisionPredicate(this);
        GrayClientProperties grayClientProperties = GrayClientAppContext.getGrayClientProperties();
        //如果与多版本一起使用
        if (grayClientProperties.getInstance().isUseMultiVersion()) {
            subDelegate = new BambooZoneAvoidanceRule();
            grayCompositePredicate = CompositePredicate.withPredicates(subDelegate.getPredicate(),
                    apiVersionPredicate).build();
        } else {
            grayCompositePredicate = CompositePredicate.withPredicates(super.getPredicate(),
                    apiVersionPredicate).build();
        }
    }


    @Override
    public Server choose(Object key) {
        BambooRequestContext requestContext = BambooRequestContext.currentRequestContext();
        if (requestContext == null || StringUtils.isEmpty(requestContext.getServiceId())) {
            return super.choose(key);
        }
        String serviceId = requestContext.getServiceId();
        ILoadBalancer lb = getLoadBalancer();
        List<Server> servers = lb.getAllServers();
        java.util.Optional<Server> offline = servers.parallelStream().filter(e -> !isOnline(serviceId, e)).findAny();
        if (offline.isPresent()) {//有下线的服务
            servers = servers.stream().filter(e -> isOnline(serviceId, e)).collect(Collectors.toList());//剔除下线服务
        }
        if (getGrayManager().isOpen(serviceId)) {//开启了灰度
            GrayService grayService = getGrayManager().grayService(serviceId);
            List<Server> grayServers = new ArrayList<>(grayService.onlineAndgrayInstances().size());
            List<Server> normalServers = new ArrayList<>(servers.size() - grayService.onlineAndgrayInstances().size());
            for (Server server : servers) {
                Map<String, String> serverMetadata = getServerMetadata(serviceId, server);
                String instanceId = ServiceUtil.getInstanceId(server, serverMetadata);
                if (grayService.getGrayInstance(instanceId) != null) {
                    grayServers.add(server);
                } else {
                    normalServers.add(server);
                }
            }
            Optional<Server> server = grayCompositePredicate.chooseRoundRobinAfterFiltering(grayServers, key);
            if (server.isPresent()) {
                return server.get();
            } else if (subDelegate != null) {//需要版本控制
                return choose(subDelegate.getPredicate(), normalServers, key);
            } else {//不需要版本控制
                return choose(super.getPredicate(), normalServers, key);
            }
        } else if (offline.isPresent()) {//有下线
            if (subDelegate != null) {//需要版本控制
                return choose(subDelegate.getPredicate(), servers, key);
            } else {//不需要版本控制
                return choose(super.getPredicate(), servers, key);
            }
        } else if (subDelegate != null) {//没有下线&&需要版本控制
            return subDelegate.choose(key);
        } else {//没有下线&&不需要版本控制
            return super.choose(key);
        }
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        super.initWithNiwsConfig(clientConfig);
        if (subDelegate != null) {
            subDelegate.initWithNiwsConfig(clientConfig);
        }
    }

    @Override
    public void setLoadBalancer(ILoadBalancer lb) {
        super.setLoadBalancer(lb);
        if (subDelegate != null) {
            subDelegate.setLoadBalancer(lb);
        }
    }

    private boolean isOnline(String serviceId, Server server) {
        Map<String, String> serverMetadata = getServerMetadata(serviceId, server);
        String instanceId = ServiceUtil.getInstanceId(server, serverMetadata);
        return getGrayManager().isOnline(serviceId, instanceId);
    }

    private Server choose(AbstractServerPredicate serverPredicate, List<Server> servers, Object key) {
        Optional<Server> server = serverPredicate.chooseRoundRobinAfterFiltering(servers, key);
        if (server.isPresent()) {
            return server.get();
        } else {
            return null;
        }
    }

    public GrayManager getGrayManager() {
        return GrayClientAppContext.getGrayManager();
    }

    public static Map<String, String> getServerMetadata(String serviceId, Server server) {
        return BambooAppContext.getEurekaServerExtractor().getServerMetadata(serviceId, server);
    }
}
