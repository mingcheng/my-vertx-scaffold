package com.gracecode.scaffold.component;

import com.gracecode.scaffold.verticle.ServerVerticle;

/**
 * @author mingcheng
 */
public interface ServiceVerticleComponent {
    /**
     * 注入依赖
     *
     * @param verticle
     */
    void inject(ServerVerticle verticle);
}
