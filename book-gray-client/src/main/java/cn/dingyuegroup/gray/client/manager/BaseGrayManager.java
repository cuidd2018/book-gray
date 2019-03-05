package cn.dingyuegroup.gray.client.manager;

import cn.dingyuegroup.gray.client.config.properties.GrayClientConfig;
import cn.dingyuegroup.gray.client.config.properties.GrayOptionalArgs;
import cn.dingyuegroup.gray.client.context.GrayClientAppContext;
import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayService;
import cn.dingyuegroup.gray.core.InstanceLocalInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 在AbstractGrayManager基础上进行了扩展，将灰度列表缓存起来，定时从灰度服务端更新灰度列表。
 */
public class BaseGrayManager extends AbstractGrayManager {
    private static final Logger log = LoggerFactory.getLogger(BaseGrayManager.class);
    private Map<String, GrayService> grayServiceMap;
    private Timer updateTimer = new Timer("Gray-UpdateTimer", true);
    private GrayClientConfig clientConfig;

    public BaseGrayManager(GrayOptionalArgs grayOptionalArgs) {
        super(grayOptionalArgs.getInformationClient(), grayOptionalArgs.getDecisionFactory());
        clientConfig = grayOptionalArgs.getGrayClientConfig();
        grayServiceMap = new ConcurrentHashMap<>();
    }


    @Override
    public void openForWork() {
        /*if (clientConfig.isGrayEnroll()) {
            grayEnroll();
        }
        updateCache();*/
        updateTimer.schedule(new UpdateTask(),
                clientConfig.getServiceUpdateIntervalTimerInMs(),
                clientConfig.getServiceUpdateIntervalTimerInMs());
    }

    @Override
    public void updateCache() {
        try {
            client.uploadInstanceLocalInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            updateGrayServices(client.listGrayService());
        } catch (Exception e) {
            log.error("更新灰度服务列表失败", e);
        }
    }

    @Override
    public List<GrayService> listGrayService() {
        if (grayServiceMap == null) {
            List<GrayService> grayServices = super.listGrayService();
            if (grayServices == null) {
                return null;
            }
            updateGrayServices(grayServices);
        }
        return new ArrayList<>(grayServiceMap.values());
    }


    @Override
    public GrayService grayService(String serviceId) {
        if (grayServiceMap == null) {
            return super.grayService(serviceId);
        }
        return grayServiceMap.get(serviceId);
    }

    @Override
    public GrayInstance grayInstance(String serviceId, String instanceId) {
        if (grayServiceMap == null) {
            return super.grayInstance(serviceId, instanceId);
        }
        GrayService grayService = grayService(serviceId);
        if (grayService != null) {
            return grayService.getGrayInstance(instanceId);
        }
        return null;
    }

    @Override
    protected void serviceShutdown() {
        updateTimer.cancel();
    }

    @Override
    public void updateGrayServices(Collection<GrayService> grayServices) {
        if (grayServices == null) {
            return;
        }
        Map<String, GrayService> grayMap = new HashMap<>();
        grayServices.forEach(grayService -> grayMap.put(grayService.getServiceId(), grayService));
        grayServiceMap = new ConcurrentHashMap(grayMap);
    }


    private void grayEnroll() {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(clientConfig.grayEnrollDealyTimeInMs());
            } catch (InterruptedException e) {
            }
            InstanceLocalInfo localInfo = GrayClientAppContext.getInstanceLocalInfo();
            try {
                client.addGrayInstance(localInfo.getServiceId(), localInfo.getInstanceId());
            } catch (Exception e) {
                log.error("自身实例灰度注册失败", e);
            }
        }, "GrayEnroll");
        t.start();
    }

    class UpdateTask extends TimerTask {

        @Override
        public void run() {
            updateCache();
        }
    }
}
