package com.gracecode.scaffold.module;

import com.gracecode.scaffold.verticle.BaseVerticle;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@Module
public class BaseVerticleModule {
    private final BaseVerticle verticle;

    public BaseVerticleModule(BaseVerticle verticle) {
        this.verticle = verticle;
    }

    @Provides
    Logger getLogger() {
        return LoggerFactory.getLogger(verticle.getClass().getSimpleName());
    }

    @Provides
    io.vertx.reactivex.core.Vertx getRxVertx() {
        return verticle.getRxVertx();
    }

    @Provides
    Vertx getVertx() {
        return verticle.getVertx();
    }
}
