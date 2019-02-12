package cn.dingyuegroup.gray.server.refresh;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 只有返回文本的接口才能使用
 *
 * @author whx
 * @date 2017/10/10 0010
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RefreshGrayClient {
}