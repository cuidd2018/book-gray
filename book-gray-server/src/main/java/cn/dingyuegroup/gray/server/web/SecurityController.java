package cn.dingyuegroup.gray.server.web;

/**
 * Created by 170147 on 2019/1/28.
 */

import cn.dingyuegroup.gray.server.web.base.BaseController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class SecurityController extends BaseController {

    @RequestMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @RequestMapping("login")
    public String login() {
        return "login";
    }

    @RequestMapping("/index")
    public String index(Model model) {
        String username = getUsername();
        model.addAttribute("username", username);
        return "index";
    }

    @RequestMapping("login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }

    /**
     * 退出登陆两种方式，一种在配置类设置，一种在这里写就不要配置了
     * <p>
     * 这里 首先我们在使用SecurityContextHolder.getContext().getAuthentication() 之前校验该用户是否已经被验证过。
     * 然后调用SecurityContextLogoutHandler().logout(request, response, auth)  来退出
     * <p>
     * logout 调用流程：
     * <p>
     * 1 将 HTTP Session 作废，解绑其绑定的所有对象。
     * <p>
     * 2 从SecurityContext移除Authentication 防止并发请求的问题。
     * <p>
     * 3 显式地清楚当前线程的上下文里的值。
     * <p>
     * 在应用的其他地方不再需要处理 退出。
     * <p>
     * 注意：你甚至都不需要在你的spring多添加任何配置（不管是基于注解还是基于xml）。
     *
     * @return
     */
    @RequestMapping("logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login";
    }

    @GetMapping("/401")
    public String accessDenied() {
        return "401";
    }
}
