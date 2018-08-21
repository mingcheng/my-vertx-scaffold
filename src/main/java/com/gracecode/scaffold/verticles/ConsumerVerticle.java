package com.gracecode.scaffold.verticles;

import com.gracecode.scaffold.GreetingServiceGrpc;
import com.gracecode.scaffold.Person;
import io.grpc.ManagedChannel;
import io.vertx.core.Future;
import io.vertx.ext.consul.Service;
import io.vertx.grpc.VertxChannelBuilder;

public class ConsumerVerticle extends BaseVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

        consulClient.catalogServiceNodes(RPC_SERVER_NAME, res -> {
            if (res.succeeded()) {
                if (res.result().getList().isEmpty()) {
                    return;
                }

                for (Service service : res.result().getList()) {
                    logger.info(String.format(
                            "Get %s Service from Consul with address/port %s:%d",
                            RPC_SERVER_NAME, service.getAddress(), service.getPort()));
                    bindGrpc(service.getAddress(), service.getPort());
                    break;
                }
            }
        });
    }

    /**
     * 发送和返回 PRC 消息
     *
     * @param host String
     * @param port int
     */
    private void bindGrpc(String host, int port) {
        ManagedChannel rpcChannel = VertxChannelBuilder
                .forAddress(vertx, host, port)
                .usePlaintext(true)
                .build();

        GreetingServiceGrpc.GreetingServiceVertxStub stub = GreetingServiceGrpc.newVertxStub(rpcChannel);
        Person person = Person.newBuilder()
                .setName(getClass().getName())
                .setAge((int) (100 * Math.random()))
                .build();

        stub.greet(person, result -> {
            if (result.succeeded()) {
                logger.info(result.result().getMessage());
            }
        });
    }
}
