package io.quarkus.resteasy.reactive.common.runtime;

import org.jboss.resteasy.reactive.spi.ThreadSetupAction;

import io.quarkus.arc.InjectableContext;
import io.quarkus.arc.ManagedContext;

public class ArcThreadSetupAction implements ThreadSetupAction {

    private final ManagedContext managedContext;

    public ArcThreadSetupAction(ManagedContext managedContext) {
        this.managedContext = managedContext;
    }

    @Override
    public ThreadState activateInitial() {
        boolean wasActiveBefore = managedContext.isActive();
        if (!wasActiveBefore) {
            managedContext.activate();
        }
        InjectableContext.ContextState state = managedContext.getState();
        return toThreadState(state, wasActiveBefore);
    }

    private ThreadState toThreadState(InjectableContext.ContextState state, boolean wasActiveBefore) {
        return new ThreadState() {
            @Override
            public void close() {
                if (!wasActiveBefore) {
                    managedContext.destroy(state);
                }
            }

            @Override
            public void activate() {
                managedContext.activate(state);
            }

            @Override
            public void deactivate() {
                managedContext.deactivate();
            }
        };
    }

    @Override
    public ThreadState currentState() {
        return toThreadState(managedContext.getState(), false);
    }
}
