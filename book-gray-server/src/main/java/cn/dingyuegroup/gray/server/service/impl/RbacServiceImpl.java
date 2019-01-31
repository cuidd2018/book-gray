package cn.dingyuegroup.gray.server.service.impl;

import cn.dingyuegroup.gray.server.config.WebSecurityConfig;
import cn.dingyuegroup.gray.server.model.vo.GrantedAuthorityVO;
import cn.dingyuegroup.gray.server.model.vo.UserDetailsVO;
import cn.dingyuegroup.gray.server.mysql.dao.GrayRbacUserMapper;
import cn.dingyuegroup.gray.server.mysql.entity.GrayRbacUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 170147 on 2019/1/30.
 */
@Service
public class RbacServiceImpl implements UserDetailsService, Serializable {

    @Autowired
    private GrayRbacUserMapper grayRbacUserMapper;

    /**
     * 校验账户
     *
     * @param s
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        GrayRbacUser grayRbacUser = grayRbacUserMapper.selectByAccount(s);
        if (grayRbacUser == null
                || StringUtils.isEmpty(grayRbacUser.getPassword())
                || StringUtils.isEmpty(grayRbacUser.getUdid())) {
            return null;
        }
        List<GrantedAuthorityVO> list = new ArrayList<GrantedAuthorityVO>() {
            {
                add(new GrantedAuthorityVO(WebSecurityConfig.ROLE_NAME));
            }
        };
        UserDetailsVO rbacUserVO = new UserDetailsVO(s, grayRbacUser.getPassword(), list);
        return rbacUserVO;
    }

}
