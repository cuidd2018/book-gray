package cn.dingyuegroup.gray.server.mysql.dao.proxy;

import cn.dingyuegroup.gray.server.mysql.dao.GrayInstancePolicyGroupMapper;
import cn.dingyuegroup.gray.server.mysql.dao.GrayPolicyGroupMapper;
import cn.dingyuegroup.gray.server.mysql.entity.GrayInstancePolicyGroup;
import cn.dingyuegroup.gray.server.mysql.entity.GrayPolicyGroupEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 170147 on 2019/1/4.
 */
@Component
public class GrayInstancePolicyGroupMapperProxy implements GrayInstancePolicyGroupMapper {
    @Autowired
    private GrayInstancePolicyGroupMapper grayInstancePolicyGroupMapper;

    @Autowired
    private GrayPolicyGroupMapper grayPolicyGroupMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return 0;
    }

    @Override
    public int insert(GrayInstancePolicyGroup record) {
        return 0;
    }

    @Override
    public GrayInstancePolicyGroup selectByPrimaryKey(Integer id) {
        return null;
    }

    @Override
    public List<GrayInstancePolicyGroup> selectAll() {
        return null;
    }

    @Override
    public int updateByPrimaryKey(GrayInstancePolicyGroup record) {
        return 0;
    }

    @Override
    public List<GrayInstancePolicyGroup> selectByInstanceId(String instanceId) {
        return null;
    }

    public List<GrayPolicyGroupEntity> selectPolicyGroupByInstanceId(String instanceId) {
        List<GrayInstancePolicyGroup> grayInstancePolicyGroups = grayInstancePolicyGroupMapper.selectByInstanceId(instanceId);
        if (CollectionUtils.isEmpty(grayInstancePolicyGroups)) {
            return null;
        }
        List<String> policyGroupIds = grayInstancePolicyGroups.stream().map(GrayInstancePolicyGroup::getPolicyGroupId).collect(Collectors.toList());
        List<GrayPolicyGroupEntity> list = grayPolicyGroupMapper.selectListByPolicyGroupId(policyGroupIds);
        return list;
    }
}
