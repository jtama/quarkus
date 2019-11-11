package io.quarkus.security.deployment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.jandex.DotName;

import io.quarkus.arc.processor.InterceptorBindingRegistrar;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 */
public class SecurityAnnotationsRegistrar implements InterceptorBindingRegistrar {

    public static final Map<DotName, Set<String>> SECURITY_BINDINGS = new HashMap<>();

    static {
        // keep the contents the same as in io.quarkus.resteasy.deployment.SecurityTransformerUtils
        SECURITY_BINDINGS.put(DotNames.ROLES_ALLOWED, Collections.singleton("value"));
        SECURITY_BINDINGS.put(DotNames.AUTHENTICATED, Collections.emptySet());
        SECURITY_BINDINGS.put(DotNames.DENY_ALL, Collections.emptySet());
        SECURITY_BINDINGS.put(DotNames.PERMIT_ALL, Collections.emptySet());
    }

    @Override
    public Map<DotName, Set<String>> registerAdditionalBindings() {
        return SECURITY_BINDINGS;
    }
}
