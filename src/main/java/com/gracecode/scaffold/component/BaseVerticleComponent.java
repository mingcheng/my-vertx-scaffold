package com.gracecode.scaffold.component;

import com.gracecode.scaffold.module.BaseVerticleModule;
import com.gracecode.scaffold.verticle.BaseVerticle;
import dagger.Component;

@Component(modules = BaseVerticleModule.class)
public interface BaseVerticleComponent {
    void inject(BaseVerticle verticle);
}
