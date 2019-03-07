package cn.dingyuegroup.gray.client.manager;

import cn.dingyuegroup.gray.core.GrayService;

import java.util.List;


/**
 * 该接口主要是负责和灰度服务端进行通信，获取灰度列表，编辑灰度实例等能力。其实现类HttpInformationClient默认使用http方式访问灰度服务端。
 * 子类InformationClientDecorator是一个适配器类，RetryableInformationClient继承了InformationClientDecorator类，实现了重试的功能。
 */
public interface InformationClient {


    /**
     * 返回在灰度中注册的所有实例(包括非灰度实例)
     *
     * @return 类度服务列表
     */
    List<GrayService> listGrayService();


    /**
     * 注册灰度实例
     */
    void addGrayInstance();


    /**
     * 灰度实例下线
     */
    void serviceDownline();


    Boolean uploadInstanceLocalInfo();

}
