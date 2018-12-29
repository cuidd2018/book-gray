package cn.dingyuegroup.gray.core;

/**
 * 灰度决策的工厂类
 */
public interface GrayDecisionFactory {


    GrayDecision getDecision(GrayPolicy grayPolicy);
}
