package cn.dingyuegroup.gray.server;

import cn.dingyuegroup.gray.server.config.GrayMQMarkerConfiguration;
import cn.dingyuegroup.gray.server.config.GrayServerMarkerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by 170147 on 2019/1/18.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = {GrayMQMarkerConfiguration.class, GrayServerMarkerConfiguration.class})
public @interface EnableGrayServerWithMQ {
}
