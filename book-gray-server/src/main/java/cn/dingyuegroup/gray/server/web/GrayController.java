package cn.dingyuegroup.gray.server.web;

import cn.dingyuegroup.gray.server.model.fo.GrayPolicyGroupFO;
import cn.dingyuegroup.gray.server.model.vo.GrayInstanceVO;
import cn.dingyuegroup.gray.server.model.vo.GrayPolicyGroupVO;
import cn.dingyuegroup.gray.server.model.vo.GrayServiceVO;
import cn.dingyuegroup.gray.server.service.AbstractGrayService;
import cn.dingyuegroup.gray.server.vertify.VertifyRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gray/manager")
public class GrayController {

    @Autowired
    private AbstractGrayService grayService;


    /**
     * 返回所有服务
     *
     * @return 灰度服务VO集合
     */
    @RequestMapping(value = "/services")
    public ResponseEntity<List<GrayServiceVO>> services() {
        List<GrayServiceVO> list = grayService.services();
        return ResponseEntity.ok(list);
    }


    /**
     * 返回服务实例列表
     *
     * @param serviceId 服务id
     * @return 灰度服务实例VO列表
     */
    @VertifyRequest
    @RequestMapping(value = "/services/instances", method = RequestMethod.GET)
    public ResponseEntity<List<GrayInstanceVO>> instances(@RequestParam("serviceId") String serviceId) {
        List<GrayInstanceVO> list = grayService.instances(serviceId);
        return ResponseEntity.ok(list);
    }


    @ApiOperation(value = "更新实例灰度状态")
    @VertifyRequest
    @RequestMapping(value = "/services/instance/status", method = RequestMethod.GET)
    public ResponseEntity<Void> editInstanceStatus(
            @RequestParam("serviceId") String serviceId,
            @RequestParam("instanceId") String instanceId,
            @ApiParam("0:关闭, 1:启用") @RequestParam("status") int status) {
        return grayService.editInstanceStatus(serviceId, instanceId, status);
    }


    /**
     * 服务实例的所有灰度策略组
     *
     * @param serviceId  服务id
     * @param instanceId 实例id
     * @return 灰策略组VO列表
     */
    @RequestMapping(value = "/services/instance/policyGroups", method = RequestMethod.GET)
    public ResponseEntity<List<GrayPolicyGroupVO>> policyGroups(@RequestParam("serviceId") String serviceId,
                                                                @RequestParam("instanceId") String instanceId) {
        return grayService.policyGroups(serviceId, instanceId);
    }


    /**
     * 灰度策略组
     *
     * @param serviceId  服务id
     * @param instanceId 实例id
     * @param groupId    灰度策略组id
     * @return 灰度策略组VO
     */
    @RequestMapping(value = "/services/{serviceId}/instance/policyGroup/{groupId}", method = RequestMethod.GET)
    public ResponseEntity<GrayPolicyGroupVO> policyGroup(@PathVariable("serviceId") String serviceId,
                                                         @RequestParam("instanceId") String instanceId,
                                                         @PathVariable("groupId") String groupId) {
        return grayService.policyGroup(serviceId, instanceId, groupId);
    }


    @ApiOperation(value = "更新实例策略组启用状态")
    @RequestMapping(value = "/services/{serviceId}/instance/policyGroup/{groupId}/status/{status}", method = RequestMethod.PUT)
    public ResponseEntity<Void> editPolicyGroupStatus(@PathVariable("serviceId") String serviceId,
                                                      @RequestParam("instanceId") String instanceId,
                                                      @PathVariable("groupId") String groupId,
                                                      @ApiParam("0:关闭, 1:启用") @PathVariable("status") int enable) {
        return grayService.editPolicyGroupStatus(serviceId, instanceId, groupId, enable);
    }


    /**
     * 添加策略组
     *
     * @param serviceId     服务id
     * @param policyGroupFO 灰度策略组FO
     * @return Void
     */
    @RequestMapping(value = "/services/{serviceId}/instance/policyGroup", method = RequestMethod.POST)
    public ResponseEntity<Void> policyGroup(
            @PathVariable("serviceId") String serviceId,
            @RequestBody GrayPolicyGroupFO policyGroupFO) {
        return grayService.policyGroup(serviceId, policyGroupFO);
    }


    /**
     * 删除策略组
     *
     * @param serviceId     服务id
     * @param instanceId    实例id
     * @param policyGroupId 灰度策略组id
     * @return Void
     */
    @ApiOperation("删除策略组")
    @RequestMapping(value = "/services/{serviceId}/instance/policyGroup", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delPolicyGroup(
            @PathVariable("serviceId") String serviceId,
            @RequestParam("instanceId") String instanceId,
            @RequestParam("groupId") String policyGroupId) {
        return grayService.delPolicyGroup(serviceId, instanceId, policyGroupId);
    }
}
