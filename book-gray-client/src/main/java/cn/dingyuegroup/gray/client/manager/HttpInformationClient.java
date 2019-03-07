package cn.dingyuegroup.gray.client.manager;

import cn.dingyuegroup.gray.client.context.GrayClientAppContext;
import cn.dingyuegroup.gray.core.GrayService;
import cn.dingyuegroup.gray.core.GrayServiceApi;
import cn.dingyuegroup.gray.core.InstanceLocalInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;

public class HttpInformationClient implements InformationClient {
    private static final Logger log = LoggerFactory.getLogger(HttpInformationClient.class);
    private final String serverName;
    private final GrayServiceApi grayServiceApi;

    public HttpInformationClient(String serverName, GrayServiceApi grayServiceApi) {
        this.grayServiceApi = grayServiceApi;
        this.serverName = "http://" + serverName;
    }

    @Override
    public List<GrayService> listGrayService() {
        log.info("\n----------------------------------------------------------\n\t"
                        + "pull instances from gray server:\n\t"
                        + "Local service: \t\t{}\n\t"
                        + "Local instance: \t\t{}\n\t"
                        + "gray service: \t\t{}\n----------------------------------------------------------",
                GrayClientAppContext.getInstanceLocalInfo().getServiceId(), GrayClientAppContext.getInstanceLocalInfo().getInstanceId(), serverName);
        try {
            List<GrayService> list = grayServiceApi.enableServices();
            log.info("\n" + "gray list: \t\t{}\n----------------------------------------------------------",
                    list == null ? null : list.toString());
            return list;
        } catch (RuntimeException e) {
            log.error("获取灰度服务列表失败", e);
            return null;
        }
    }

    @Override
    public Boolean uploadInstanceLocalInfo() {
        log.info("\n----------------------------------------------------------\n\t"
                + "upload service enviroment to gray server\n\t----------------------------------------------------------");
        try {
            InstanceLocalInfo localInfo = GrayClientAppContext.getInstanceLocalInfo();
            if (localInfo == null || StringUtils.isEmpty(localInfo.getServiceId()) || StringUtils.isEmpty(localInfo.getInstanceId())) {
                log.error("upload service enviroment to gray error,localInfo data missing:{}", localInfo);
                return false;
            }
            if (GrayClientAppContext.getEnvironment() == null || GrayClientAppContext.getEnvironment().getActiveProfiles().length == 0) {
                log.error("upload service enviroment to gray error,enviroment data missing:{}", localInfo);
                return false;
            }
            localInfo.setEnv(GrayClientAppContext.getEnvironment().getActiveProfiles()[0]);
            grayServiceApi.uploadInstanceInfo(localInfo);
            log.info("\n----------------------------------------------------------\n\t"
                    + "upload service enviroment to gray server done\n\t----------------------------------------------------------");
        } catch (RuntimeException e) {
            log.info("upload service enviroment to gray error", e);
            throw e;
        }
        return false;
    }

    @Override
    public void addGrayInstance() {
        try {
            InstanceLocalInfo localInfo = GrayClientAppContext.getInstanceLocalInfo();
            grayServiceApi.onlineInstance(localInfo.getServiceId(), localInfo.getInstanceId());
        } catch (RuntimeException e) {
            log.error("灰度服务实例下线失败", e);
            throw e;
        }
    }

    @Override
    public void serviceDownline() {
        try {
            InstanceLocalInfo localInfo = GrayClientAppContext.getInstanceLocalInfo();
            grayServiceApi.offlineInstance(localInfo.getServiceId(), localInfo.getInstanceId());
        } catch (Exception e) {
            log.error("灰度服务实例下线失败", e);
            throw e;
        }
    }
}
