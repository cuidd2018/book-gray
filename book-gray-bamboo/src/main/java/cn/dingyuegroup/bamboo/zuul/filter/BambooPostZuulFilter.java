package cn.dingyuegroup.bamboo.zuul.filter;

import cn.dingyuegroup.bamboo.config.properties.BambooAppContext;
import com.netflix.zuul.ZuulFilter;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * 做一些善后工作。比如删除BambooRequestContext在ThreadLocal中的信息。
 */
public class BambooPostZuulFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
//        BambooRequestContext.shutdownRequestContext();
        BambooAppContext.getBambooRibbonConnectionPoint().shutdownconnectPoint();
        return null;
    }
}
