package com.gracecode.scaffold.service.impl;

import com.gracecode.scaffold.Greet;
import com.gracecode.scaffold.GreetingServiceGrpc;
import com.gracecode.scaffold.Person;
import com.gracecode.scaffold.verticle.ServerVerticle;
import io.vertx.core.Future;

import javax.inject.Inject;

/**
 * @author mingcheng
 */
public class GrpcServiceImpl extends GreetingServiceGrpc.GreetingServiceVertxImplBase {
    public static final String RPC_SERVER_NAME = ServerVerticle.class.getSimpleName();

    @Inject
    public GrpcServiceImpl() {

    }

    @Override
    public void greet(Person person, Future<Greet> response) {
        Greet.Builder greet = Greet.newBuilder().setMessage(
                String.format("Geeting from %s with age %d", person.getName(), person.getAge())
        );
        response.complete(greet.build());
    }
}
