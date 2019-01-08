package cn.dingyuegroup.gray.server.manager;

import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayService;

import java.util.List;

/**
 * Created by 170147 on 2019/1/8.
 */

public interface GrayServiceManager2 {
    /**
     * 获取全部的灰度服务实例
     *
     * @return
     */
    List<GrayService> getServices();

    /**
     * 获取服务信息
     *
     * @param serviceId
     * @return
     */
    GrayService getGrayService(String serviceId);

    /**
     * 获取某服务下的所有服务实例
     *
     * @param serviceId
     * @return
     */
    List<GrayInstance> getInstances(String serviceId);

    /**
     * 获取某个服务实例的信息
     *
     * @param instanceId
     * @return
     */
    GrayInstance getGrayInstance(String serviceId, String instanceId);

    /**
     * 更新服务实例的灰度状态
     *
     * @param serviceId
     * @param instanceId
     * @param status
     * @return
     */
    boolean editInstanceStatus(String serviceId, String instanceId, int status);

    /**
     * 更新策略组的状态
     *
     * @param serviceId
     * @param instanceId
     * @param groupId
     * @param enable
     * @return
     */
    boolean editPolicyGroupStatus(String serviceId, String instanceId, String groupId, int enable);

    /**
     * 添加策略组
     *
     * @param alias
     * @param enable
     * @return
     */
    boolean addPolicyGroup(String alias, Integer enable);

    /**
     * 删除策略组
     *
     * @return
     */
    boolean delPolicyGroup(String groupId);

    /**
     * 编辑策略组
     *
     * @param groupId
     * @param alias
     * @param enable
     * @return
     */
    boolean editPolicyGroup(String groupId, String alias, Integer enable);

    /**
     * 添加策略
     *
     * @return
     */
    boolean addPolicy(String policyType, String policy);

    /**
     * 编辑策略
     *
     * @param policyId
     * @param policyType
     * @param policy
     * @return
     */
    boolean editPolicy(String policyId, String policyType, String policy);

    /**
     * 删除策略组
     *
     * @return
     */
    boolean delPolicy(String policyId);

    /**
     * 策略组添加策略
     *
     * @param groupId
     * @param policyId
     * @return
     */
    boolean addPolicyGroupPolicy(String groupId, String policyId);

    /**
     * 策略组删除策略
     *
     * @param groupId
     * @param policyId
     * @return
     */
    boolean delPolicyGroupPolicy(String groupId, String policyId);

    /**
     * 服务实例添加灰度策略组
     *
     * @param serviceId
     * @return
     */
    boolean addInstancePolicyGroup(String serviceId, String instanceId, String groupId);

    /**
     * 服务实例删除灰度策略组
     *
     * @param serviceId
     * @param instanceId
     * @param groupId
     * @return
     */
    boolean delInstancePolicyGroup(String serviceId, String instanceId, String groupId);
}
