package cn.dingyuegroup.gray.client.utils;

import com.netflix.loadbalancer.Server;

import java.util.Map;

public class ServiceUtil {
    private static final String METADATA_KEY_INSTANCE_ID = "instanceId";

    public static String getInstanceId(Server server, Map<String, String> serverMetadata) {
        return server.getMetaInfo().getInstanceId();
    }
}
