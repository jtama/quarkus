package io.quarkus.csrf.reactive.runtime;

import org.jboss.resteasy.reactive.server.core.Deployment;
import org.jboss.resteasy.reactive.server.spi.ServerRestHandler;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class CsrfRecorder {

    public void configure(RuntimeValue<Deployment> deployment, CsrfReactiveConfig csrfReactiveConfig) {
        for (ServerRestHandler handler : deployment.getValue().getRuntimeConfigurableServerRestHandlers()) {
            if (handler instanceof CsrfHandler) {
                ((CsrfHandler) handler).configure(csrfReactiveConfig);
                break;
            }
        }
    }

}
