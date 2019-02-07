package cn.dingyuegroup.gray.server.manager;

import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayPolicyGroup;
import cn.dingyuegroup.gray.core.GrayService;
import cn.dingyuegroup.gray.server.model.vo.GrayPolicyGroupVO;
import cn.dingyuegroup.gray.server.model.vo.GrayPolicyVO;

import java.util.List;

/**
 * Created by 170147 on 2019/1/8.
 */

public interface GrayServiceManager {
    /**
     * 获取全部的灰度服务实例
     *
     * @return
     */
    List<GrayService> getServices();

    /**
     * 添加服务
     *
     * @param appName
     * @param serviceId
     * @param remark
     */
    void addService(String appName, String serviceId, String remark);

    /**
     * 编辑服务
     *
     * @param appName
     * @param serviceId
     * @param remark
     */
    void editService(String appName, String serviceId, String remark);

    /**
     * 删除服务
     *
     * @param serviceId
     */
    void deleteService(String serviceId);

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
     * 编辑服务实例
     *
     * @param serviceId
     * @param instanceId
     * @param remark
     */
    void editInstance(String serviceId, String instanceId, String remark);

    /**
     * 删除服务实例
     *
     * @param serviceId
     * @param instanceId
     */
    void deleteInstance(String serviceId, String instanceId);

    /**
     * 更新服务实例的灰度状态
     *
     * @param serviceId
     * @param instanceId
     * @param status
     * @return
     */
    boolean editInstanceGrayStatus(String serviceId, String instanceId, int status);

    /**
     * 更新服务实例的在线状态
     *
     * @param serviceId
     * @param instanceId
     * @param status
     * @return
     */
    boolean editInstanceOnlineStatus(String serviceId, String instanceId, int status);

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
    boolean addPolicyGroup(String alias, Integer enable, String groupType, String remark);

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
     * @return
     */
    boolean editPolicyGroup(String groupId, String alias, String groupType, String remark);

    /**
     * 添加策略
     *
     * @return
     */
    boolean addPolicy(String policyType, String policyKey, String policyValue, String policyMatchType);

    /**
     * 编辑策略
     *
     * @param policyId
     * @param policyType
     * @return
     */
    boolean editPolicy(String policyId, String policyType, String policyKey, String policyValue, String policyMatchType);

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
    boolean editInstancePolicyGroup(String serviceId, String instanceId, String groupId);

    /**
     * 服务实例删除灰度策略组
     *
     * @param serviceId
     * @param instanceId
     * @param groupId
     * @return
     */
    boolean delInstancePolicyGroup(String serviceId, String instanceId, String groupId);

    /**
     * 获取策略组下的灰度策略
     *
     * @param groupId
     * @return
     */
    List<GrayPolicyVO> listGrayPolicyByGroup(String groupId);

    /**
     * 获取所有灰度策略
     *
     * @return
     */
    List<GrayPolicyVO> listAllGrayPolicy();

    /**
     * 获取所有策略组列表
     *
     * @return
     */
    List<GrayPolicyGroupVO> listAllGrayPolicyGroup();

    /**
     * 获取服务实例下的灰度策略组
     *
     * @param serviceId
     * @param instanceId
     * @return
     */
    GrayPolicyGroup getGrayPolicyGroup(String serviceId, String instanceId);

    /**
     * 打开检查
     */
    void openForWork();


    void shutdown();
}
