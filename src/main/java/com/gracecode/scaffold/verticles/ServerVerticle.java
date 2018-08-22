package com.gracecode.scaffold.verticles;

import com.gracecode.scaffold.Greet;
import com.gracecode.scaffold.GreetingServiceGrpc;
import com.gracecode.scaffold.Person;
import io.reactivex.Single;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;
import io.vertx.reactivex.core.impl.AsyncResultSingle;

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

        new AsyncResultSingle<Void>(handler -> {
            rpcServer.start(handler);
        }).flatMap(handler -> {
            logger.info("Start Greeting RPC is successful.");
            ServiceOptions options = new ServiceOptions()
                    .setName(RPC_SERVER_NAME)
                    .setAddress(getGrpcHost())
                    .setPort(getGrpcPort());

            return new AsyncResultSingle<Void>(s -> {
                consulClient.registerService(options, s);
            });
        }).subscribe(result -> {
            logger.info(String.format("Registered Greeting Service to Consul with name: [%s]", RPC_SERVER_NAME));
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
            })
                    .flatMap(handler -> new AsyncResultSingle<Void>(s -> {
                        consulClient.deregisterService(RPC_SERVER_NAME, s);
                    }))
                    .subscribe(result -> {
                        logger.info("Deregistering rpc service from Consul.");
                        consulClient.close();
                    }, error -> {
                        logger.info("Close Consul server connect.");
                    });
        }
    }
}
