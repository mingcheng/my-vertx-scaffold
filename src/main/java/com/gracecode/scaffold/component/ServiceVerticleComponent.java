package com.gracecode.scaffold.component;

import com.gracecode.scaffold.module.ServerVerticleModule;
import com.gracecode.scaffold.verticle.ServerVerticle;
import dagger.Component;

/**
 * @author mingcheng
 */

@Component(modules = ServerVerticleModule.class)
public interface ServiceVerticleComponent {
    /**
     * 注入依赖
     *
     * @param verticle
     */
    void inject(ServerVerticle verticle);
}
