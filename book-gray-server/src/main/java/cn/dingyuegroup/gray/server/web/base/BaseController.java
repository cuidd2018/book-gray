package cn.dingyuegroup.gray.server.web.base;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by 170147 on 2019/2/1.
 */
public abstract class BaseController {
    /**
     * 取登录用户名
     *
     * @return
     */
    public String getUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails != null) {
            return userDetails.getUsername();
        }
        return null;
    }

}
