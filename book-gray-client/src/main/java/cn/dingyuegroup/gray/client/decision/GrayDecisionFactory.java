package cn.dingyuegroup.gray.client.decision;

import cn.dingyuegroup.gray.core.GrayPolicy;

/**
 * 灰度决策的工厂类
 */
public interface GrayDecisionFactory {


    GrayDecision getDecision(GrayPolicy grayPolicy);
}
