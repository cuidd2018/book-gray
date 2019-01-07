package cn.dingyuegroup.gray.server.mysql.dao.proxy;

import cn.dingyuegroup.gray.server.mysql.dao.GrayInstancePolicyGroupMapper;
import cn.dingyuegroup.gray.server.mysql.dao.GrayPolicyGroupMapper;
import cn.dingyuegroup.gray.server.mysql.dao.GrayPolicyGroupPolicyMapper;
import cn.dingyuegroup.gray.server.mysql.dao.GrayPolicyMapper;
import cn.dingyuegroup.gray.server.mysql.entity.GrayInstancePolicyGroup;
import cn.dingyuegroup.gray.server.mysql.entity.GrayPolicyEntity;
import cn.dingyuegroup.gray.server.mysql.entity.GrayPolicyGroupEntity;
import cn.dingyuegroup.gray.server.mysql.entity.GrayPolicyGroupPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 170147 on 2019/1/4.
 */
@Component
@Qualifier("GrayInstancePolicyGroupMapperProxy")
public class GrayInstancePolicyGroupMapperProxy implements GrayInstancePolicyGroupMapper {
    @Autowired
    private GrayInstancePolicyGroupMapper grayInstancePolicyGroupMapper;

    @Autowired
    private GrayPolicyGroupMapper grayPolicyGroupMapper;

    @Autowired
    private GrayPolicyMapper grayPolicyMapper;

    @Autowired
    private GrayPolicyGroupPolicyMapper grayPolicyGroupPolicyMapper;

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

    @Override
    public List<GrayPolicyGroupEntity> selectPolicyGroupByInstanceId(String instanceId) {
        //获取服务实例下的策略组集合
        List<GrayInstancePolicyGroup> grayInstancePolicyGroups = grayInstancePolicyGroupMapper.selectByInstanceId(instanceId);
        if (CollectionUtils.isEmpty(grayInstancePolicyGroups)) {
            return null;
        }
        //获取策略组信息
        List<GrayPolicyGroupEntity> list = new ArrayList<>();
        grayInstancePolicyGroups.stream().forEach(e -> {
            GrayPolicyGroupEntity grayPolicyGroupEntity = grayPolicyGroupMapper.selectByPolicyGroupId(e.getPolicyGroupId());
            if (grayPolicyGroupEntity != null) {
                list.add(grayPolicyGroupEntity);
            }
        });
        list.stream().forEach(e -> {
            //获取策略组和策略的对应关系
            List<GrayPolicyGroupPolicy> grayPolicyGroupPolicies = grayPolicyGroupPolicyMapper.selectByPolicyGroupId(e.getPolicyGroupId());
            grayPolicyGroupPolicies.stream().forEach(f -> {
                //获取每个策略的具体信息
                GrayPolicyEntity grayPolicyEntity = grayPolicyMapper.selectByPolicyId(f.getPolicyId());
                if (grayPolicyEntity != null) {
                    e.getGrayPolicyEntities().add(grayPolicyEntity);
                }
            });
        });
        return list;
    }
}
