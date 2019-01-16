package cn.dingyuegroup.gray.core;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


/**
 * 注册的服务， 维护一个实例列表。
 */
@ToString
@Data
public class GrayService {

    private String appName;
    private String serviceId;
    private List<GrayInstance> grayInstances = new ArrayList<>();
    private boolean status;//是否在线

    public GrayService() {

    }

    /**
     * 判断service是否在线
     *
     * @return
     */
    public boolean isOnline() {
        if (getGrayInstances() == null || getGrayInstances().isEmpty()) {
            return false;
        }
        Optional<GrayInstance> optional = getGrayInstances().parallelStream().filter(e -> e.isStatus()).findAny();
        return optional.isPresent();
    }

    public void addGrayInstance(GrayInstance grayInstance) {
        grayInstances.add(grayInstance);
    }

    public GrayInstance getGrayInstance(String instanceId) {
        Iterator<GrayInstance> iter = grayInstances.iterator();
        while (iter.hasNext()) {
            GrayInstance instance = iter.next();
            if (instance.getInstanceId().equals(instanceId)) {
                return instance;
            }
        }
        return null;
    }

    public boolean isOpenGray() {
        return getGrayInstances() != null
                && !getGrayInstances().isEmpty()
                && hasGrayInstance();
    }

    public boolean hasGrayInstance() {
        for (GrayInstance grayInstance : getGrayInstances()) {
            if (grayInstance.isOpenGray()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasGrayPolicy() {
        for (GrayInstance grayInstance : getGrayInstances()) {
            if (grayInstance.hasGrayPolicy()) {
                return true;
            }
        }
        return false;
    }

    public GrayService toNewGrayService() {
        GrayService newService = new GrayService();
        newService.setServiceId(serviceId);
        newService.setStatus(status);
        newService.setAppName(appName);
        return newService;
    }

    public GrayService takeNewOpenGrayService() {
        GrayService service = toNewGrayService();
        for (GrayInstance grayInstance : grayInstances) {
            if (grayInstance.isOpenGray() && grayInstance.isStatus()) {//开启灰度，且在线
                service.addGrayInstance(grayInstance.takeNewOpenGrayInstance());
            }
        }
        return service;
    }
}
