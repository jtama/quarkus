package io.quarkus.vertx.http.runtime.security;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import io.quarkus.security.spi.runtime.IdentityProviderManagerBuilder;
import io.quarkus.vertx.runtime.VertxRecorder;

@Singleton
public class IdentityProviderManagerBuilderCustomizerProducer {

    @Produces
    @Singleton
    public IdentityProviderManagerBuilder.Customizer produce() {
        return new IdentityProviderManagerBuilder.Customizer() {
            @Override
            public void customize(IdentityProviderManagerBuilder<?> builder) {
                builder.setEventLoopExecutor(VertxRecorder.getVertx().nettyEventLoopGroup());
            }
        };
    }
}
