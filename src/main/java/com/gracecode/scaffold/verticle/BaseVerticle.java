package com.gracecode.scaffold.verticle;

import com.gracecode.scaffold.component.DaggerBaseVerticleComponent;
import com.gracecode.scaffold.module.BaseVerticleModule;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.consul.ConsulClient;

import javax.inject.Inject;


public abstract class BaseVerticle extends AbstractVerticle {
    private static final String KEY_DEBUG = "debug";

    /**
     * 配置字段，参见 application.json
     */
    private static final String KEY_SERVICES = "services";
    private static final String KEY_CONSUL = "consul";
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";
    private static final String KEY_GREETING = "greeting";
    static final String RPC_SERVER_NAME = ServerVerticle.class.getSimpleName();

    @Inject
    Logger logger;

    /**
     * Consul Service
     */
    ConsulClient consulClient;


    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        DaggerBaseVerticleComponent.builder()
                .baseVerticleModule(new BaseVerticleModule(this))
                .build().inject(this);

        if (isDebugMode()) {
            logger.isDebugEnabled();
        }

        ConsulClientOptions options = new ConsulClientOptions()
                .setHost(getConsulHost()).setPort(getConsulPort());

        consulClient = ConsulClient.create(this.vertx, options);
        consulClient.rxAgentInfo()
                .subscribe(result -> {
                    if (isDebugMode()) {
                        logger.info(result);
                    }
                }, error -> {
                    logger.fatal(error.getMessage(), error);
                });
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);
    }


    /**
     * 根据配置判断是否在开发模式
     *
     * @return Boolean
     */
    boolean isDebugMode() {
        try {
            return config().getBoolean(KEY_DEBUG);
        } catch (RuntimeException e) {
            return true;
        }
    }

    int getGrpcPort() {
        return getGrpcConfig().getInteger(KEY_PORT);
    }

    String getGrpcHost() {
        return getGrpcConfig().getString(KEY_HOST);
    }

    private JsonObject getGrpcConfig() {
        return getServicesConfig().getJsonObject(KEY_GREETING);
    }

    String getConsulHost() {
        return getConsulConfig().getString(KEY_HOST);
    }

    int getConsulPort() {
        return getConsulConfig().getInteger(KEY_PORT);
    }

    private JsonObject getServicesConfig() {
        return config().getJsonObject(KEY_SERVICES);
    }

    private JsonObject getConsulConfig() {
        return getServicesConfig().getJsonObject(KEY_CONSUL);
    }

    public io.vertx.reactivex.core.Vertx getRxVertx() {
        return vertx;
    }
}
