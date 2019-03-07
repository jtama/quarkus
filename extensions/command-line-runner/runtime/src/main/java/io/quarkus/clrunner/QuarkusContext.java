package io.quarkus.clrunner;

import io.quarkus.arc.runtime.BeanContainer;

public class QuarkusContext {

    private final BeanContainer beanContainer;

    public QuarkusContext(BeanContainer beanContainer) {
        this.beanContainer = beanContainer;
    }

    public String name() {
        return "Quarkus";
    }

    public BeanContainer getBeanContainer() {
        return beanContainer;
    }
}
