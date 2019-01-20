package cn.dingyuegroup.gray.client.decision;

import cn.dingyuegroup.bamboo.BambooRequest;
import cn.dingyuegroup.gray.core.GrayDecision;
import cn.dingyuegroup.gray.core.GrayPolicy;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.regex.Pattern;

public class RequestParameterDecision implements GrayDecision {


    private final MultiValueMap<String, String> params;

    public RequestParameterDecision(MultiValueMap<String, String> params) {
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
            Map<String, String> map = params.toSingleValueMap();
            String value = bambooRequest.getParams().toSingleValueMap().get(map.get(GrayPolicy.POLICY.POLICY_KEY.name()));
            if (value != null) {
                if (map.get(GrayPolicy.POLICY.POLICY_MATCH_TYPE.name()).equals(MATCH_TYPE.EQUAL.name())) {//匹配规则是相等
                    return value.equals(map.get(GrayPolicy.POLICY.POLICY_VALUE.name()));
                } else if (map.get(GrayPolicy.POLICY.POLICY_MATCH_TYPE.name()).equals(MATCH_TYPE.REGEX.name())) {//匹配规则是正则
                    return Pattern.compile(map.get(GrayPolicy.POLICY.POLICY_VALUE.name())).matcher(value).matches();
                }
            }
        }
        return false;
    }
}
