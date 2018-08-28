package com.gracecode.scaffold.verticle;

import com.gracecode.scaffold.Greet;
import com.gracecode.scaffold.GreetingServiceGrpc;
import com.gracecode.scaffold.Person;
import com.gracecode.scaffold.service.impl.GrpcServiceImpl;
import io.grpc.ManagedChannel;
import io.reactivex.Single;
import io.vertx.core.Future;
import io.vertx.grpc.VertxChannelBuilder;
import io.vertx.reactivex.core.impl.AsyncResultSingle;

import java.util.Random;

/**
 * @author mingcheng
 */
public class ConsumerVerticle extends BaseVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

        consulClient.rxCatalogServiceNodes(GrpcServiceImpl.RPC_SERVER_NAME)
                .flatMap(result -> {
                    if (result.getList().isEmpty()) {
                        return Single.error(
                                new Throwable(String.format("Not found %s address on Consul.", GrpcServiceImpl.RPC_SERVER_NAME))
                        );
                    } else {
                        // Only get the first record.
                        return Single.just(result.getList().get(0));
                    }
                })
                .subscribe(service -> {
                    logger.info(String.format(
                            "Get %s Service from Consul with address/port %s:%d",
                            GrpcServiceImpl.RPC_SERVER_NAME, service.getAddress(), service.getPort()));
                    bindGrpc(service.getAddress(), service.getPort());
                }, error -> {
                    logger.error(error.getMessage(), error);
                });
    }

    /**
     * 发送和返回 RPC 消息
     *
     * @param host String
     * @param port int
     */
    private void bindGrpc(String host, int port) {
        new AsyncResultSingle<Greet>(handler -> {
            ManagedChannel rpcChannel = VertxChannelBuilder
                    .forAddress(getVertx(), host, port)
                    .usePlaintext(true)
                    .build();

            GreetingServiceGrpc.GreetingServiceVertxStub stub = GreetingServiceGrpc.newVertxStub(rpcChannel);

            Person person = Person.newBuilder()
                    .setName(getClass().getName())
                    .setAge(new Random().nextInt())
                    .build();

            stub.greet(person, handler);
        }).subscribe(p -> {
            logger.info(p.getMessage());
        }, error -> {
            logger.error(error);
        });
    }
}
