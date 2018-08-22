package com.gracecode.scaffold.verticles;

import com.gracecode.scaffold.Greet;
import com.gracecode.scaffold.GreetingServiceGrpc;
import com.gracecode.scaffold.Person;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;

public class ServerVerticle extends BaseVerticle {
    /**
     * 处理 gRPC 的返回
     */
    private GreetingServiceGrpc.GreetingServiceVertxImplBase greetingServiceImpl
            = new GreetingServiceGrpc.GreetingServiceVertxImplBase() {
        @Override
        public void greet(Person person, Future<Greet> response) {
            Greet.Builder greet = Greet.newBuilder().setMessage(
                    String.format("Geeting from %s with age %d", person.getName(), person.getAge())
            );
            response.complete(greet.build());
        }
    };

    /**
     * gRPC 引用，使用了 Vertx 的封装
     */
    private VertxServer rpcServer;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        if (isDebugMode()) {
            logger.info("Initialize ServerVerticle with debug mode.");
        }


        if (isDebugMode()) {
            logger.info(String.format("Consul Configure with %s:%d", getConsulHost(), getConsulPort()));
        }


    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

        rpcServer = VertxServerBuilder.forAddress(getVertx(), getGrpcHost(), getGrpcPort())
                .addService(greetingServiceImpl).build();

        rpcServer.start(result -> {
            if (result.succeeded()) {
                logger.info("Start Greeting RPC is successful.");

                // @TODO with rx
                consulClient.registerService(
                        new ServiceOptions()
                                .setName(RPC_SERVER_NAME)
                                .setAddress(getGrpcHost())
                                .setPort(getGrpcPort()),
                        consulResult -> {
                            if (consulResult.succeeded()) {
                                logger.info(String.format("Registered Greeting Server to Consul with name: %s", RPC_SERVER_NAME));
                            } else {
                                logger.info(consulResult.cause().getMessage(), consulResult.cause());
                            }
                        });
            } else {
                logger.fatal(result.cause().getMessage(), result.cause());
            }
        });
    }


    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);

        if (!rpcServer.isShutdown()) {
            logger.info("Shutting down rpc service.");
            rpcServer.shutdown(result -> {
                if (result.succeeded()) {
                    logger.info("Deregistering rpc service from Consul.");
                    consulClient.deregisterService(RPC_SERVER_NAME, v -> {
                        // do nothing.
                    });
                }

                logger.info("Close Consul server connect.");
                consulClient.close();
            });
        }
    }
}
