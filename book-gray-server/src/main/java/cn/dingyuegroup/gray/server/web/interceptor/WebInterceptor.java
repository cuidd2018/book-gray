package cn.dingyuegroup.gray.server.web.interceptor;

import cn.dingyuegroup.gray.server.manager.RbacManager;
import cn.dingyuegroup.gray.server.model.vo.GrayUserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 170147 on 2019/2/5.
 */
public class WebInterceptor extends HandlerInterceptorAdapter {

    Logger logger = LoggerFactory.getLogger(getClass());

    final RbacManager rbacManager;

    public WebInterceptor(RbacManager rbacManager) {
        super();
        this.rbacManager = rbacManager;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView mav)
            throws Exception {
        try {
            if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
                return;
            }
            Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (object != null && object instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) object;
                String username = userDetails.getUsername();
                GrayUserVO grayUserVO = rbacManager.getDepartment(username);
                mav.getModel().put("user", grayUserVO);
            }
        } catch (Exception e) {
            logger.error("WebInterceptor", e);
        }
    }
}
