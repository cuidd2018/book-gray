package cn.dingyuegroup.bamboo.zuul.filter;

import cn.dingyuegroup.bamboo.BambooAppContext;
import cn.dingyuegroup.bamboo.BambooRequest;
import cn.dingyuegroup.bamboo.ConnectPointContext;
import cn.dingyuegroup.bamboo.autoconfig.properties.BambooProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;

/**
 * 主要作用是用来获取request的相关信息，为后面的路由提供数据基础。
 */
public class BambooPreZuulFilter extends ZuulFilter {

    private static final Logger log = LoggerFactory.getLogger(BambooPreZuulFilter.class);

    private BambooProperties bambooProperties;

    public BambooPreZuulFilter(BambooProperties bambooProperties) {
        this.bambooProperties = bambooProperties;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 10000;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        BambooRequest.Builder builder = BambooRequest.builder()
                .serviceId((String) context.get(FilterConstants.SERVICE_ID_KEY))
                .uri((String) context.get(FilterConstants.REQUEST_URI_KEY))
                .ip(context.getZuulRequestHeaders().get(FilterConstants.X_FORWARDED_FOR_HEADER.toLowerCase()))
                .addMultiParams(context.getRequestQueryParams())
                .addHeaders(context.getZuulRequestHeaders());

        // add http server request header
        HttpServletRequest servletRequest = context.getRequest();
        Enumeration<String> headerNames = servletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            builder.addHeader(headerName, servletRequest.getHeader(headerName));
        }

        if (bambooProperties.getBambooRequest().isLoadBody()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(context.getRequest().getInputStream()));
                byte[] reqBody = IOUtils.toByteArray(reader);
                builder.requestBody(reqBody);
            } catch (IOException e) {
                String errorMsg = "获取request body出现异常";
                log.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        }

        ConnectPointContext connectPointContext = ConnectPointContext.builder().bambooRequest(builder.build()).build();

        BambooAppContext.getBambooRibbonConnectionPoint().executeConnectPoint(connectPointContext);
        return null;
    }

}
