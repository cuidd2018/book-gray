package cn.dingyuegroup.gray.client.decision;

import cn.dingyuegroup.bamboo.context.BambooRequest;

/**
 * 组合多个灰度策略
 */
public class MultiGrayDecision implements GrayDecision {

    private GrayDecision decision;


    public MultiGrayDecision(GrayDecision decision) {
        this.decision = decision;
    }


    public MultiGrayDecision and(GrayDecision other) {
        GrayDecision cur = decision;
        decision = t -> cur.test(t) && other.test(t);
        return this;
    }

    public MultiGrayDecision or(GrayDecision other) {
        GrayDecision cur = decision;
        decision = t -> cur.test(t) || other.test(t);
        return this;
    }

    @Override
    public boolean test(BambooRequest bambooRequest) {
        return decision.test(bambooRequest);
    }

}
