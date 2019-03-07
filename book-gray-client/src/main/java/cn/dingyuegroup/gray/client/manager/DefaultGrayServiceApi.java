package cn.dingyuegroup.gray.client.manager;

import cn.dingyuegroup.gray.core.GrayServiceApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Created by 170147 on 2019/3/6.
 */
@FeignClient(name = "${gray.client.server-name}")
public interface DefaultGrayServiceApi extends GrayServiceApi {
}
