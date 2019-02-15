package cn.dingyuegroup.gray.server.config;

import cn.dingyuegroup.gray.server.web.interceptor.WebInterceptor;
import cn.dingyuegroup.gray.server.manager.RbacManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Autowired
    private RbacManager rbacManager;

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new CORSFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new WebInterceptor(rbacManager)).addPathPatterns("/**");
    }


    public class CORSFilter implements Filter {

        private String allowMethods;
        private String allowHeaders;
        private String exposeHeaders;

        public CORSFilter() {
            this("POST, PUT, GET, OPTIONS, DELETE",
                    "x-requested-with,Authorization,Origin,X-Requested-With,Content-Type,Accept"
            );
        }

        public CORSFilter(String allowMethods, String allowHeaders) {
            this(allowMethods, allowHeaders, allowHeaders);
        }

        public CORSFilter(String allowMethods, String allowHeaders, String exposeHeaders) {
            this.allowMethods = allowMethods;
            this.allowHeaders = allowHeaders;
            this.exposeHeaders = exposeHeaders;
        }

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            // TODO Auto-generated method stub

        }


        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
                throws IOException, ServletException {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
//        String origin = (String) servletRequest.getRemoteHost()+":"+servletRequest.getRemotePort();
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", allowMethods);
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", allowHeaders);
            response.setHeader("Access-Control-Expose-Headers", allowHeaders);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            filterChain.doFilter(servletRequest, servletResponse);
        }

        @Override
        public void destroy() {
            // TODO Auto-generated method stub

        }
    }
}
