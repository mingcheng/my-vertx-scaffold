package com.gracecode.scaffold.module;

import com.gracecode.scaffold.verticle.BaseVerticle;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.reactivex.ext.consul.ConsulClient;

/** @author mingcheng */
@Module
public class BaseVerticleModule {
  private final BaseVerticle verticle;

  static final String KEY_HOST = "host";
  static final String KEY_PORT = "port";

  /** 配置字段，参见 application.json */
  private static final String KEY_SERVICES = "services";

  private static final String KEY_CONSUL = "consul";

  public BaseVerticleModule(BaseVerticle verticle) {
    this.verticle = verticle;
  }

  @Provides
  Logger provideLogger() {
    return LoggerFactory.getLogger(verticle.getClass().getSimpleName());
  }

  @Provides
  io.vertx.reactivex.core.Vertx provideRxVertx() {
    return verticle.getRxVertx();
  }

  @Provides
  Vertx provideVertx() {
    return verticle.getVertx();
  }

  @Provides
  ConsulClient provideConsulClient() {
    ConsulClientOptions options =
        new ConsulClientOptions().setHost(getConsulHost()).setPort(getConsulPort());

    return ConsulClient.create(provideRxVertx(), options);
  }

  String getConsulHost() {
    return getConsulConfig().getString(KEY_HOST);
  }

  int getConsulPort() {
    return getConsulConfig().getInteger(KEY_PORT);
  }

  JsonObject getServicesConfig() {
    return verticle.config().getJsonObject(KEY_SERVICES);
  }

  boolean isDebugMode() {
    return verticle.isDebugMode();
  }

  private JsonObject getConsulConfig() {
    return getServicesConfig().getJsonObject(KEY_CONSUL);
  }
}
