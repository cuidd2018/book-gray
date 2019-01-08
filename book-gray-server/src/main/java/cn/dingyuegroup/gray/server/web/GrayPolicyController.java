package cn.dingyuegroup.gray.server.web;

import cn.dingyuegroup.gray.server.manager.GrayServiceManager2;
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
    private GrayServiceManager2 grayServiceManager2;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public ResponseEntity<Void> addPolicy(@RequestParam("policyType") String policyType, @RequestParam("policy") String policy) {
        grayServiceManager2.addPolicy(policyType, policy);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public ResponseEntity<Void> editPolicy(@RequestParam("policyType") String policyType, @RequestParam("policy") String policy, @RequestParam String policyId) {
        grayServiceManager2.editPolicy(policyId, policyType, policy);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public ResponseEntity<Void> deletePolicy(@RequestParam String policyId) {
        grayServiceManager2.delPolicy(policyId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/group/add", method = RequestMethod.GET)
    public ResponseEntity<Void> addGroup(@RequestParam String alias, @RequestParam Integer enable) {
        grayServiceManager2.addPolicyGroup(alias, enable);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/group/edit", method = RequestMethod.GET)
    public ResponseEntity<Void> editGroup(@RequestParam String groupId, @RequestParam String alias, @RequestParam Integer enable) {
        grayServiceManager2.editPolicyGroup(groupId, alias, enable);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/group/delete", method = RequestMethod.GET)
    public ResponseEntity<Void> deleteGroup(@RequestParam String groupId) {
        grayServiceManager2.delPolicyGroup(groupId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/group/relate", method = RequestMethod.GET)
    public ResponseEntity<Void> relateGroup(@RequestParam String groupId, @RequestParam String policyId) {
        grayServiceManager2.addPolicyGroupPolicy(groupId, policyId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/group/unRelate", method = RequestMethod.GET)
    public ResponseEntity<Void> unRelateGroup(@RequestParam String groupId, @RequestParam String policyId) {
        grayServiceManager2.delPolicyGroupPolicy(groupId, policyId);
        return ResponseEntity.ok().build();
    }
}
