#适用场景
* 目前只支持Eureka作为注册中心，ribbon作为负载均衡，hystrix做为断路器的springcloud微服务体系

#产品功能
* 支持按比例灵活控制灰度流量，实现平滑灰度
* 支持白名单控制灰度用户
* 支持一键下线功能，重启服务不会造成用户连接闪断
* 支持基本的RBAC权限控制，资源相互隔离，确保资源安全

#项目介绍
* [book-gray-bamboo] 截获灰度策略控制参数，为灰度客户端负载均衡路由提供数据支持
* [book-gray-client] 灰度客户端，实现灰度策略
* [book-gray-core] 灰度组件核心类定义
* [book-gray-server] 灰度服务端，定义灰度策略，控制灰度流量，控制服务在线状态
* [book-gray-starter-client] 对外提供灰度客户端支持
* [book-gray-starter-server] 对外提供灰度服务端支持
* [book-gray-samples] 灰度组件使用demo

#使用方式
* 灰度客户端
    （1）在项目启动类添加注解@EnableGrayClient即可
    （2）如果要实现灰度策略的实时刷新，在项目启动类添加注解@EnableGrayClientWithMQ，并配置MQ资源
* 灰度服务端
    （1）在项目启动类添加注解@EnableGrayServer，并配置灰度策略持久化mysql资源
    （2）如果要实现灰度策略的实时下发，在项目启动类添加注解@EnableGrayServerWithMQ，并配置MQ资源
    （3）如果灰度服务端要部署多节点，则需要在项目启动类添加注解@EnableShareSession，并配置redis资源
