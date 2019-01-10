package cn.dingyuegroup.gray.server.web;

import cn.dingyuegroup.gray.server.manager.GrayServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 170147 on 2019/1/8.
 */
@RestController
@RequestMapping("/gray/manager/policy")
public class GrayPolicyController {

    @Autowired
    private GrayServiceManager grayServiceManager;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public ResponseEntity<Void> addPolicy(@RequestParam("policyType") String policyType, @RequestParam("policy") String policy) {
        grayServiceManager.addPolicy(policyType, policy);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public ResponseEntity<Void> editPolicy(@RequestParam("policyType") String policyType, @RequestParam("policy") String policy, @RequestParam String policyId) {
        grayServiceManager.editPolicy(policyId, policyType, policy);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public ResponseEntity<Void> deletePolicy(@RequestParam String policyId) {
        grayServiceManager.delPolicy(policyId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/group/add", method = RequestMethod.GET)
    public ResponseEntity<Void> addGroup(@RequestParam String alias, @RequestParam Integer enable) {
        grayServiceManager.addPolicyGroup(alias, enable);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/group/edit", method = RequestMethod.GET)
    public ResponseEntity<Void> editGroup(@RequestParam String groupId, @RequestParam String alias, @RequestParam Integer enable) {
        grayServiceManager.editPolicyGroup(groupId, alias, enable);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/group/delete", method = RequestMethod.GET)
    public ResponseEntity<Void> deleteGroup(@RequestParam String groupId) {
        grayServiceManager.delPolicyGroup(groupId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/group/relate", method = RequestMethod.GET)
    public ResponseEntity<Void> relateGroup(@RequestParam String groupId, @RequestParam String policyId) {
        grayServiceManager.addPolicyGroupPolicy(groupId, policyId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/group/unRelate", method = RequestMethod.GET)
    public ResponseEntity<Void> unRelateGroup(@RequestParam String groupId, @RequestParam String policyId) {
        grayServiceManager.delPolicyGroupPolicy(groupId, policyId);
        return ResponseEntity.ok().build();
    }
}
