package cn.dingyuegroup.gray.client;

import cn.dingyuegroup.gray.client.config.GrayClientMarkerConfiguration;
import cn.dingyuegroup.gray.client.config.GrayMQMarkerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(value = {GrayMQMarkerConfiguration.class, GrayClientMarkerConfiguration.class})
public @interface EnableGrayClientWithMQ {

}
