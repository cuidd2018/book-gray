package cn.dingyuegroup.gray.core;

import lombok.Data;
import lombok.ToString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * 灰度策略组，有状态属性
 */
@ToString
@Data
public class GrayPolicyGroup {

    private String policyGroupId;
    private String alias;
    private List<GrayPolicy> list = new ArrayList<>();
    private boolean enable = true;
    private String groupType;//策略组类型，与和或

    public enum TYPE {
        AND,
        OR
    }

    public void addGrayPolicy(GrayPolicy policy) {
        removeGrayPolicy(policy.getPolicyId());
        list.add(policy);
    }

    public GrayPolicy removeGrayPolicy(String policyId) {
        Iterator<GrayPolicy> iter = list.iterator();
        while (iter.hasNext()) {
            GrayPolicy policy = iter.next();
            if (policy.getPolicyId().equals(policyId)) {
                iter.remove();
                return policy;
            }
        }
        return null;
    }

    public GrayPolicy getGrayPolicy(String policyId) {
        for (GrayPolicy grayPolicy : list) {
            if (grayPolicy.getPolicyId().equals(policyId)) {
                return grayPolicy;
            }
        }
        return null;
    }

    public static String genId() {
        return "POLICY_GROUP_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
}
