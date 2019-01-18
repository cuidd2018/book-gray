package cn.dingyuegroup.gray.server.config;

import cn.dingyuegroup.gray.server.GrayServer;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = {GrayServerMarkerConfiguration.class, GrayServer.class})
public @interface EnableGrayServer {
}
