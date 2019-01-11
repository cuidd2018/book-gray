package cn.dingyuegroup.gray.core;

import lombok.ToString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 注册的服务， 维护一个实例列表。
 */
@ToString
public class GrayService {

    private String appName;
    private String serviceId;
    private List<GrayInstance> grayInstances = new ArrayList<>();
    private boolean status;//是否在线


    public GrayService() {

    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public List<GrayInstance> getGrayInstances() {
        return grayInstances;
    }

    public void setGrayInstances(List<GrayInstance> grayInstances) {
        this.grayInstances = grayInstances;
    }


    public boolean contains(String instanceId) {
        for (GrayInstance grayInstance : grayInstances) {
            if (grayInstance.getInstanceId().equals(instanceId)) {
                return true;
            }
        }
        return false;
    }

    public void addGrayInstance(GrayInstance grayInstance) {
        grayInstances.add(grayInstance);
    }

    public GrayInstance removeGrayInstance(String instanceId) {
        Iterator<GrayInstance> iter = grayInstances.iterator();
        while (iter.hasNext()) {
            GrayInstance instance = iter.next();
            if (instance.getInstanceId().equals(instanceId)) {
                iter.remove();
                return instance;
            }
        }
        return null;
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

    public int countGrayPolicy() {
        int count = 0;
        for (GrayInstance grayInstance : grayInstances) {
            count += grayInstance.countGrayPolicy();
        }
        return count;
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
            if (grayInstance.isOpenGray()) {
                service.addGrayInstance(grayInstance.takeNewOpenGrayInstance());
            }
        }
        return service;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
