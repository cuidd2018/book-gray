package cn.dingyuegroup.gray.server.service;

import cn.dingyuegroup.gray.core.GrayInstance;
import cn.dingyuegroup.gray.core.GrayService;
import cn.dingyuegroup.gray.server.mysql.dao.GrayServiceMapper;
import cn.dingyuegroup.gray.server.mysql.entity.GrayServiceEntity;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 170147 on 2019/1/9.
 */
public abstract class AbstractGrayService {

    private final DiscoveryClient discoveryClient;
    private final GrayServiceMapper grayServiceMapper;

    public AbstractGrayService(DiscoveryClient discoveryClient, GrayServiceMapper grayServiceMapper) {
        this.discoveryClient = discoveryClient;
        this.grayServiceMapper = grayServiceMapper;
    }

    /**
     * 获取所有在线的服务
     *
     * @return
     */
    public List<GrayService> upServices() {
        List<String> upServiceIds = upServiceIds();
        List<GrayService> list = new ArrayList<>();
        upServiceIds.stream().forEach(e -> {
            GrayService grayService = new GrayService();
            grayService.setServiceId(e);
            GrayServiceEntity entity = grayServiceMapper.selectByServiceId(e);
            if (entity != null) {
                grayService.setAppName(entity.getAppName());
            }
            grayService.setStatus(true);//在线
            List<GrayInstance> grayInstances = upInstances(e);
            grayService.setGrayInstances(grayInstances);
            list.add(grayService);
        });
        return list;
    }

    public List<String> upServiceIds() {
        List<String> upServiceIds = discoveryClient.getServices();
        if (CollectionUtils.isEmpty(upServiceIds)) {
            return new ArrayList<>();
        }
        return upServiceIds;
    }

    public abstract List<String> upInstanceIds(String serviceId);

    public abstract List<GrayInstance> upInstances(String serviceId);
}
