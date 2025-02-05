package io.quarkus.redis.client.deployment;

import static io.quarkus.redis.client.deployment.RedisClientProcessor.REDIS_CLIENT_ANNOTATION;
import static io.quarkus.redis.client.deployment.RedisClientProcessor.configureAndCreateSyntheticBean;
import static io.quarkus.redis.client.deployment.RedisClientProcessor.configuredClientNames;
import static io.quarkus.redis.runtime.client.config.RedisConfig.DEFAULT_CLIENT_NAME;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;

import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.BeanDiscoveryFinishedBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.processor.InjectionPointInfo;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.runtime.client.RedisClientRecorder;
import io.quarkus.vertx.deployment.VertxBuildItem;

public class RedisDatasourceProcessor {

    private static final List<DotName> SUPPORTED_INJECTION_TYPE = List.of(
            DotName.createSimple(RedisDataSource.class.getName()),
            DotName.createSimple(ReactiveRedisDataSource.class.getName()));

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    public void init(RedisClientRecorder recorder,
            RedisBuildTimeConfig buildTimeConfig,
            BeanArchiveIndexBuildItem indexBuildItem,
            BeanDiscoveryFinishedBuildItem beans,
            ShutdownContextBuildItem shutdown,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeans,
            VertxBuildItem vertxBuildItem) {

        // Collect the used redis datasource, the unused clients will not be instantiated.
        Set<String> names = new HashSet<>();
        IndexView indexView = indexBuildItem.getIndex();
        Collection<AnnotationInstance> clientAnnotations = indexView.getAnnotations(REDIS_CLIENT_ANNOTATION);
        for (AnnotationInstance annotation : clientAnnotations) {
            names.add(annotation.value().asString());
        }

        // Check if the application use the default Redis datasource.
        beans.getInjectionPoints().stream().filter(InjectionPointInfo::hasDefaultedQualifier)
                .filter(i -> SUPPORTED_INJECTION_TYPE.contains(i.getRequiredType().name()))
                .findAny()
                .ifPresent(x -> names.add(DEFAULT_CLIENT_NAME));

        beans.getInjectionPoints().stream()
                .filter(i -> SUPPORTED_INJECTION_TYPE.contains(i.getRequiredType().name()))
                .filter(InjectionPointInfo::isProgrammaticLookup)
                .findAny()
                .ifPresent(x -> names.addAll(configuredClientNames(buildTimeConfig, ConfigProvider.getConfig())));

        // Inject the creation of the client when the application starts.
        recorder.initialize(vertxBuildItem.getVertx(), names);

        // Create the supplier and define the beans.
        for (String name : names) {
            // Data sources
            syntheticBeans.produce(configureAndCreateSyntheticBean(name, RedisDataSource.class,
                    recorder.getBlockingDataSource(name)));
            syntheticBeans.produce(configureAndCreateSyntheticBean(name, ReactiveRedisDataSource.class,
                    recorder.getReactiveDataSource(name)));
        }

        recorder.cleanup(shutdown);
    }
}
