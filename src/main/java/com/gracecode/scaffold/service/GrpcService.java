package com.gracecode.scaffold.service;

import com.gracecode.scaffold.service.impl.GrpcServiceImpl;
import com.gracecode.scaffold.verticle.BaseVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;

/**
 * @author mingcheng
 */
public class GrpcService {
    private static final String KEY_GREETING = "greeting";

    static final String KEY_HOST = "host";
    static final String KEY_PORT = "port";
    static final String KEY_SERVICES = "services";

    private final VertxServer vertxServer;
    private final BaseVerticle verticle;

    private JsonObject getGrpcConfig() {
        return getServicesConfig().getJsonObject(KEY_GREETING);
    }

    JsonObject getServicesConfig() {
        return verticle.config().getJsonObject(KEY_SERVICES);
    }

    public int getGrpcPort() {
        return getGrpcConfig().getInteger(KEY_PORT);
    }

    public String getGrpcHost() {
        return getGrpcConfig().getString(KEY_HOST);
    }

    public GrpcService(BaseVerticle verticle, GrpcServiceImpl service) {
        this.verticle = verticle;
        this.vertxServer = VertxServerBuilder.forAddress(verticle.getVertx(), getGrpcHost(), getGrpcPort())
                .addService(service).build();
    }

    public void start(Handler<AsyncResult<Void>> handler) {
        vertxServer.start(handler);
    }

    public boolean isShutdown() {
        return vertxServer.isShutdown();
    }

    public void shutdown(Handler<AsyncResult<Void>> handler) {
        vertxServer.shutdown(handler);
    }
}
