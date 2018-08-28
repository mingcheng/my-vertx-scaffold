package com.gracecode.scaffold.module;

import com.gracecode.scaffold.service.GrpcServer;
import com.gracecode.scaffold.service.impl.GrpcServiceImpl;
import com.gracecode.scaffold.verticle.ServerVerticle;
import dagger.Module;
import dagger.Provides;

/**
 * @author mingcheng
 */
@Module
public class ServerVerticleModule extends BaseVerticleModule {
    private final ServerVerticle vertcile;

    public ServerVerticleModule(ServerVerticle verticle) {
        super(verticle);
        this.vertcile = verticle;
    }

    @Provides
    GrpcServer provideGrpcServer(GrpcServiceImpl service) {
        return new GrpcServer(vertcile, service);
    }

    @Provides
    GrpcServiceImpl provideGrpcServiceImpl() {
        return new GrpcServiceImpl();
    }
}
