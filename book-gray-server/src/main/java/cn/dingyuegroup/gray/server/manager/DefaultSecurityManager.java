package cn.dingyuegroup.gray.server.manager;

import cn.dingyuegroup.gray.server.context.GrayServerContext;
import cn.dingyuegroup.gray.server.model.bo.GrantedAuthorityBO;
import cn.dingyuegroup.gray.server.model.bo.UserDetailsBO;
import cn.dingyuegroup.gray.server.mysql.dao.GrayRbacUserMapper;
import cn.dingyuegroup.gray.server.mysql.entity.GrayRbacUser;
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
public class DefaultSecurityManager implements UserDetailsService, Serializable {

    /*
    诡异：自动注入的字段无法序列化
    org.springframework.data.redis.serializer.SerializationException: Cannot serialize; nested exception is org.springframework.core.serializer.support.SerializationFailedException: Failed to serialize object using DefaultSerializer; nested exception is java.io.NotSerializableException: java.lang.reflect.Method
    @Autowired
    private GrayRbacUserMapper grayRbacUserMapper;*/

    /**
     * 校验账户
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        GrayRbacUserMapper grayRbacUserMapper = GrayServerContext.getGrayRbacUserMapper();
        GrayRbacUser grayRbacUser = grayRbacUserMapper.selectByAccount(username);
        if (grayRbacUser == null
                || StringUtils.isEmpty(grayRbacUser.getPassword())
                || StringUtils.isEmpty(grayRbacUser.getUdid())) {
            return null;
        }
        List<GrantedAuthorityBO> list = new ArrayList<GrantedAuthorityBO>() {
            {
                add(new GrantedAuthorityBO("ROLE_USER"));
            }
        };
        UserDetailsBO rbacUserVO = new UserDetailsBO(username, grayRbacUser.getPassword(), list);
        return rbacUserVO;
    }

}
