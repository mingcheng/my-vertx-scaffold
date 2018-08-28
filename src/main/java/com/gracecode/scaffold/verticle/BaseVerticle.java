package com.gracecode.scaffold.verticle;

import com.gracecode.scaffold.component.DaggerBaseVerticleComponent;
import com.gracecode.scaffold.module.BaseVerticleModule;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.consul.ConsulClient;

import javax.inject.Inject;

/**
 * @author mingcheng
 */
public abstract class BaseVerticle extends AbstractVerticle {
    private static final String KEY_DEBUG = "debug";

    @Inject
    Logger logger;

    /**
     * Consul Service
     */
    @Inject
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
    }


    /**
     * 根据配置判断是否在开发模式
     *
     * @return Boolean
     */
    public boolean isDebugMode() {
        try {
            return config().getBoolean(KEY_DEBUG);
        } catch (RuntimeException e) {
            return true;
        }
    }


    /**
     * 结合返回 Reactive Vertx 的引用
     *
     * @return vertx
     */
    public io.vertx.reactivex.core.Vertx getRxVertx() {
        return vertx;
    }


    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

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
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);
    }
}
