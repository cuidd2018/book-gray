package cn.dingyuegroup.gray.client.decision;

import cn.dingyuegroup.bamboo.BambooRequest;
import cn.dingyuegroup.gray.core.GrayDecision;
import cn.dingyuegroup.gray.core.GrayPolicy;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PatternMatchUtils;

import java.util.List;
import java.util.Map;

public class RequestParameterDecision implements GrayDecision {


    private final Map<String, String> params;

    public RequestParameterDecision(Map<String, String> params) {
        if (params.isEmpty()) {
            throw new NullPointerException("params must not be empty");
        }
        this.params = params;
    }

    @Override
    public boolean test(BambooRequest bambooRequest) {
        if (!CollectionUtils.isEmpty(params)
                && bambooRequest.getParams() != null
                && params.containsKey(GrayPolicy.POLICY.POLICY_KEY.name())
                && params.containsKey(GrayPolicy.POLICY.POLICY_VALUE.name())
                && params.containsKey(GrayPolicy.POLICY.POLICY_MATCH_TYPE.name())) {
            List<String> reqValues = bambooRequest.getParams().get(params.get(GrayPolicy.POLICY.POLICY_KEY.name()));
            if (!CollectionUtils.isEmpty(reqValues)) {
                if (params.get(GrayPolicy.POLICY.POLICY_MATCH_TYPE.name()).equals(MATCH_TYPE.EQUAL.name())) {//匹配规则是相等
                    return reqValues.get(0).equals(params.get(GrayPolicy.POLICY.POLICY_VALUE.name()));
                } else if (params.get(GrayPolicy.POLICY.POLICY_MATCH_TYPE.name()).equals(MATCH_TYPE.REGEX.name())) {//匹配规则是正则
                    return PatternMatchUtils.simpleMatch(params.get(GrayPolicy.POLICY.POLICY_VALUE.name()), reqValues.get(0));
                }
            }
        }
        return false;
    }
}
