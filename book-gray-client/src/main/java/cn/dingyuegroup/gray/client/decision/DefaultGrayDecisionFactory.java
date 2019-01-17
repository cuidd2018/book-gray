package cn.dingyuegroup.gray.client.decision;

import cn.dingyuegroup.gray.core.GrayDecision;
import cn.dingyuegroup.gray.core.GrayDecisionFactory;
import cn.dingyuegroup.gray.core.GrayPolicy;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

/**
 * 默认的灰度决策工厂类，其默认实现类支持上述几种灰度决策的创建。
 */
public class DefaultGrayDecisionFactory implements GrayDecisionFactory {


    @Override
    public GrayDecision getDecision(GrayPolicy grayPolicy) {
        PolicyType policyType = PolicyType.valueOf(grayPolicy.getPolicyType());
        if (policyType == null) {
            throw new IllegalArgumentException("not suppot");
        }
        switch (policyType) {
            case REQUEST_IP:
                String ipstr = grayPolicy.getInfos().get(RequestIpDecision.IPS_KEY);
                return new RequestIpDecision(Arrays.asList(ipstr.split(",")));
            case REQUEST_HEADER:
                MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
                headers.setAll(grayPolicy.getInfos());
                return new RequestHeaderDecision(headers);
            case REQUEST_PARAMETER:
                return new RequestParameterDecision(grayPolicy.getInfos());
            case CONTEXT_PARAMS:
                return new ContextParameterDecision(grayPolicy.getInfos());
            default:
                throw new IllegalArgumentException("not suppot");
        }
    }
}
