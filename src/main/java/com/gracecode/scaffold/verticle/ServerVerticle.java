package com.gracecode.scaffold.verticle;

import com.gracecode.scaffold.service.GrpcServer;
import com.gracecode.scaffold.service.impl.GrpcServiceImpl;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.reactivex.core.impl.AsyncResultSingle;

import javax.inject.Inject;

/**
 * @author mingcheng
 */
public class ServerVerticle extends BaseVerticle {
    /**
     * gRPC 引用，使用了 Vertx 的封装
     */

    @Inject
    GrpcServer rpcServer;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        if (isDebugMode()) {
            logger.info("Initialize ServerVerticle with debug mode.");
        }
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

        new AsyncResultSingle<Void>(handler -> {
            rpcServer.start(handler);
        }).flatMap(handler -> {
            logger.info("Start Greeting RPC is successful.");
            ServiceOptions options = new ServiceOptions()
                    .setName(GrpcServiceImpl.RPC_SERVER_NAME)
                    .setAddress(rpcServer.getGrpcHost())
                    .setPort(rpcServer.getGrpcPort());

            return new AsyncResultSingle<Void>(s -> {
                consulClient.registerService(options, s);
            });
        }).subscribe(result -> {
            logger.info(String.format("Registered Greeting Service to Consul with name: [%s]",
                    GrpcServiceImpl.RPC_SERVER_NAME));
        }, error -> {
            logger.fatal(error);
        });
    }


    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);

        if (!rpcServer.isShutdown()) {
            logger.info("Shutting down rpc service.");
            new AsyncResultSingle<Void>(handler -> {
                rpcServer.shutdown(handler);
            }).flatMap(handler -> new AsyncResultSingle<Void>(s -> {
                consulClient.deregisterService(GrpcServiceImpl.RPC_SERVER_NAME, s);
            })).subscribe(result -> {
                logger.info("Deregistering rpc service from Consul.");
                consulClient.close();
            }, error -> {
                logger.info("Close Consul server connect.");
            });
        }
    }
}
