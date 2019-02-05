package cn.dingyuegroup.gray.server.web.base;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by 170147 on 2019/2/1.
 */
public abstract class BaseController {

    private Logger logger = Logger.getLogger(getClass());

    /**
     * 取登录用户名
     *
     * @return
     */
    public String getUsername() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userDetails != null) {
                return userDetails.getUsername();
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }
}
