package io.quarkus.arc.runtime;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.interceptor.InvocationContext;

import io.quarkus.arc.AbstractAnnotationLiteral;
import io.quarkus.arc.ArcInvocationContext;

public class InterceptorBindings {

    @SuppressWarnings("unchecked")
    public static Set<Annotation> getInterceptorBindings(InvocationContext invocationContext) {
        return (Set<Annotation>) invocationContext.getContextData().get(ArcInvocationContext.KEY_INTERCEPTOR_BINDINGS);
    }

    @SuppressWarnings("unchecked")
    public static Set<AbstractAnnotationLiteral> getInterceptorBindingLiterals(InvocationContext invocationContext) {
        return (Set<AbstractAnnotationLiteral>) invocationContext.getContextData()
                .get(ArcInvocationContext.KEY_INTERCEPTOR_BINDINGS);
    }
}
