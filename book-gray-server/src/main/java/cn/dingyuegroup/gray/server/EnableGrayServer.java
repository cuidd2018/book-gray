package cn.dingyuegroup.gray.server;

import cn.dingyuegroup.gray.server.config.GrayServerMarkerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = {GrayServerMarkerConfiguration.class})
public @interface EnableGrayServer {
}
