package cn.dingyuegroup.gray.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 灰度实例，有状态属性
 */
public class GrayInstance {
    private String appName;
    private String serviceId;
    private String instanceId;

    /**
     * 类度策略组
     */
    private List<GrayPolicyGroup> grayPolicyGroups = new ArrayList<>();
    private boolean openGray = true;
    private boolean status = false;//是否在线
    private String url;
    private Map<String, String> metadata;

    public GrayInstance() {
    }

    public GrayInstance(String serviceId, String instanceId, boolean openGray, List<GrayPolicyGroup> grayPolicyGroups) {
        this.serviceId = serviceId;
        this.instanceId = instanceId;
        this.openGray = openGray;
        this.grayPolicyGroups = grayPolicyGroups;
    }


    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public List<GrayPolicyGroup> getGrayPolicyGroups() {
        return grayPolicyGroups;
    }

    public void setGrayPolicyGroups(List<GrayPolicyGroup> grayPolicyGroups) {
        this.grayPolicyGroups = grayPolicyGroups;
    }

    public void addGrayPolicyGroup(GrayPolicyGroup policyGroup) {
        if (getGrayPolicyGroup(policyGroup.getPolicyGroupId()) == null) {
            this.grayPolicyGroups.add(policyGroup);
        }
    }

    public GrayPolicyGroup removeGrayPolicyGroup(String policyGroupId) {
        Iterator<GrayPolicyGroup> iter = grayPolicyGroups.iterator();
        while (iter.hasNext()) {
            GrayPolicyGroup policyGroup = iter.next();
            if (policyGroup.getPolicyGroupId().equals(policyGroupId)) {
                iter.remove();
                return policyGroup;
            }
        }
        return null;
    }

    public void addGrayPolicy(String policyGroupId, GrayPolicy policy) {
        GrayPolicyGroup policyGroup = getGrayPolicyGroup(policyGroupId);
        if (policyGroup != null) {
            policyGroup.addGrayPolicy(policy);
        }
    }

    public void removeGrayPolicy(String policyGroupId, String policyId) {
        GrayPolicyGroup policyGroup = getGrayPolicyGroup(policyGroupId);
        if (policyGroup != null) {
            policyGroup.removeGrayPolicy(policyId);
        }
    }

    public boolean containsPolicyGroup(String policyGroupId) {
        return getGrayPolicyGroup(policyGroupId) != null;
    }

    public GrayPolicyGroup getGrayPolicyGroup(String policyGroupId) {
        for (GrayPolicyGroup grayPolicyGroup : grayPolicyGroups) {
            if (grayPolicyGroup.getPolicyGroupId().equals(policyGroupId)) {
                return grayPolicyGroup;
            }
        }
        return null;
    }


    public boolean isOpenGray() {
        return openGray;
    }

    public void setOpenGray(boolean openGray) {
        this.openGray = openGray;
    }

    public boolean hasGrayPolicy() {
        for (GrayPolicyGroup policyGroup : getGrayPolicyGroups()) {
            if (policyGroup.getList() != null || policyGroup.getList().size() > 0) {
                return true;
            }
        }
        return false;
    }


    public int countGrayPolicy() {
        int count = 0;
        for (GrayPolicyGroup policyGroup : getGrayPolicyGroups()) {
            count += policyGroup.getList().size();
        }
        return count;
    }

    public GrayInstance toNewGrayInstance() {
        GrayInstance newInstance = new GrayInstance();
        newInstance.setInstanceId(instanceId);
        newInstance.setServiceId(serviceId);
        newInstance.setOpenGray(openGray);
        return newInstance;
    }


    public GrayInstance takeNewOpenGrayInstance() {
        GrayInstance instance = toNewGrayInstance();
        for (GrayPolicyGroup grayPolicyGroup : grayPolicyGroups) {
            if (grayPolicyGroup.isEnable()) {
                instance.addGrayPolicyGroup(grayPolicyGroup);
            }
        }
        return instance;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
