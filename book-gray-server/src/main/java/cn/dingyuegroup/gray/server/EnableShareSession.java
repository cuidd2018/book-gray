package cn.dingyuegroup.gray.server;

import cn.dingyuegroup.gray.server.config.GrayShareSessionMarkerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by 170147 on 2019/1/18.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = {GrayShareSessionMarkerConfiguration.class})
public @interface EnableShareSession {
}
