# 基于 Vert.x 的 SOA 脚手架

在搭建 SOA 服务中，免不了会使用多个技术融合以及选型，对于架构这块应该更关注于业务的本身，所以就会有脚手架的东西。

## 框架技术栈

基本的服务和类库使用到了如下：

1. [Consul](https://www.consul.io/)
2. [Vert.x](https://vertx.io/)
3. [gRPC](https://grpc.io/)

其中，Consul 用作服务发现以及配置统一，每个 Vert.x 的 `BaseVerticle` 都扩展了使用 Consul 服务发现的能力，同时 gPRC 用作节点之间的 RPC 通讯。

![sequence.png](asserts/sequence.png)

在本脚手架的例子中，分别使用了两个 Verticle 支撑了三者的链路关系。`ServerVerticle` 实现了 PRC 的服务端，同时广播给 Consul 对应自己的地址和端口。`ConsumerVerticle` 从 Consul 获得 `ServerVerticle` 注册 RPC 服务并调用输出。

## 使用说明

使用 gradle 直接 build 即可，具体参见 `gradle tasks` 命令。

### 配置

配置文件在 `src` 的 `resources` 目录中，使用 JSON 格式管理，脚手架中使用 `application.json` 全局定义了响应的配置，其中主要定义了 Consul 端以及 RPC 的地址信息。

注意，默认 `Verticle` 不读取响应的配置，对应的配置目前在 `Launcher` 中获取以及重载。

### 运行

使用 gradle jar 构建 JAR 包，然后依次运行启动 Server 端以及 Consumer 端的两个 Verticle。

推荐使用 `Launcher` 启动 `Verticle`，因为它可以除了加载配置以外，还可以做额外的初始化操作，这里脚手架已经简单的封装好了，直接使用即可。

详细的配置信息，可以参考 `gradle.build` 中的相关配置。

```
java -jar build/libs/my-vertx-scaffold-1.0.0-SNAPSHOT.jar \
    run com.gracecode.scaffold.verticles.ServerVerticle

java -jar build/libs/my-vertx-scaffold-1.0.0-SNAPSHOT.jar  \
    run com.gracecode.scaffold.verticles.ConsumerVerticle
```

![consumer.png](asserts/consumer.png)

当看见 `Consumer` 端显示正确的返回 RPC 信息，则说明这个脚手架可以使用了。

### 测试

测试框架选型使用了 Spock，它是个基于 Goovy 的优雅的测试框架。

待完善

## 思考思路

待完善

## @TODO

待完善
