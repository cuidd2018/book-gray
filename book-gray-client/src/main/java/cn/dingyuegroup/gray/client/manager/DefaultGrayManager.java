package cn.dingyuegroup.gray.client.manager;

import cn.dingyuegroup.gray.client.config.properties.GrayClientConfig;
import cn.dingyuegroup.gray.client.config.properties.GrayOptionalArgs;
import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 在AbstractGrayManager基础上进行了扩展，将灰度列表缓存起来，定时从灰度服务端更新灰度列表。
 */
public class DefaultGrayManager extends AbstractGrayManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultGrayManager.class);
    private Map<String, GrayService> grayServiceMap;
    private Timer updateTimer = new Timer("Gray-UpdateTimer", true);
    private GrayClientConfig clientConfig;

    public DefaultGrayManager(GrayOptionalArgs grayOptionalArgs) {
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
            updateGrayServices(super.listGrayService());
        } catch (Exception e) {
            log.error("更新灰度服务列表失败", e);
        }
    }

    @Override
    public List<GrayService> listGrayService() {
        if (grayServiceMap == null) {
            updateGrayServices(super.listGrayService());
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

    private void updateGrayServices(Collection<GrayService> grayServices) {
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
            try {
                updateCache();
                client.addGrayInstance();
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
