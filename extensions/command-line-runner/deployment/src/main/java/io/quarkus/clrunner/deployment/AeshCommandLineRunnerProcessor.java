package io.quarkus.clrunner.deployment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;

import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.clrunner.QuarkusCommand;
import io.quarkus.clrunner.runtime.AeshCommandLineRunnerTemplate;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.MainArgsBuildItem;
import io.quarkus.deployment.builditem.ShutdownBuildItem;
import io.quarkus.deployment.builditem.substrate.ReflectiveClassBuildItem;
import io.quarkus.deployment.recording.RecorderContext;

public class AeshCommandLineRunnerProcessor {

    private static final DotName QUARKUS_COMMAND = DotName.createSimple(QuarkusCommand.class.getName());

    @BuildStep
    List<AeshCommandLineRunnerBuildItem> discover(CombinedIndexBuildItem combinedIndexBuildItem) {
        final List<AeshCommandLineRunnerBuildItem> result = new ArrayList<>();

        for (ClassInfo info : combinedIndexBuildItem.getIndex().getAllKnownImplementors(QUARKUS_COMMAND)) {
            final DotName name = info.name();

            result.add(new AeshCommandLineRunnerBuildItem(name.toString()));
        }
        return result;
    }

    @BuildStep
    List<ReflectiveClassBuildItem> reflection(List<AeshCommandLineRunnerBuildItem> commands) {
        final List<ReflectiveClassBuildItem> result = new ArrayList<>();
        for (AeshCommandLineRunnerBuildItem command : commands) {
            result.add(new ReflectiveClassBuildItem(true, true, command.getClassName()));
        }
        return result;
    }

    @BuildStep
    void shutdown(List<AeshCommandLineRunnerBuildItem> commands, BuildProducer<ShutdownBuildItem> producer) {
        // TODO: this has to be conditional only set when various things are not present
        if (!commands.isEmpty()) {
            producer.produce(new ShutdownBuildItem());
        }
    }

    @BuildStep
    @Record(ExecutionTime.AFTER_STARTUP)
    public void commands(List<AeshCommandLineRunnerBuildItem> commands,
            BeanContainerBuildItem beanContainerBuildItem,
            MainArgsBuildItem mainArgsBuildItem,
            AeshCommandLineRunnerTemplate template,
            RecorderContext context) {

        if (commands.isEmpty()) {
            return;
        }

        Set<Class<? extends QuarkusCommand>> commandClasses = new HashSet<>();
        for (AeshCommandLineRunnerBuildItem runner : commands) {
            commandClasses.add((Class<? extends QuarkusCommand>) context.classProxy(runner.getClassName()));
        }
        template.run(commandClasses, beanContainerBuildItem.getValue(), mainArgsBuildItem);
    }
}
