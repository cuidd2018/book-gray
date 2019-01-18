package cn.dingyuegroup.gray.server.config;

import java.lang.annotation.*;

/**
 * Created by 170147 on 2019/1/18.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableGrayMQ
@EnableGrayServer
public @interface EnableGrayServerWithMQ {
}
