package io.quarkus.clrunner.runtime;

import java.io.IOException;
import java.util.Set;

import org.aesh.command.AeshCommandRuntimeBuilder;
import org.aesh.command.CommandException;
import org.aesh.command.CommandNotFoundException;
import org.aesh.command.CommandRuntime;
import org.aesh.command.impl.registry.AeshCommandRegistryBuilder;
import org.aesh.command.parser.CommandLineParserException;
import org.aesh.command.registry.CommandRegistry;
import org.aesh.command.registry.CommandRegistryException;
import org.aesh.command.validator.CommandValidatorException;
import org.aesh.command.validator.OptionValidatorException;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.clrunner.QuarkusCommand;
import io.quarkus.clrunner.QuarkusCommandInvocation;
import io.quarkus.clrunner.QuarkusCommandInvocationProvider;
import io.quarkus.clrunner.QuarkusContext;
import io.quarkus.runtime.MainArgsSupplier;
import io.quarkus.runtime.annotations.Template;

@Template
public class AeshCommandLineRunnerTemplate {

    public void run(Set<Class<? extends QuarkusCommand>> commands, BeanContainer beanContainer,
            MainArgsSupplier mainArgsSupplier) {

        try {
            final QuarkusContext quarkusContext = new QuarkusContext(beanContainer);

            final CommandRegistry<QuarkusCommandInvocation> registry = AeshCommandRegistryBuilder
                    .<QuarkusCommandInvocation> builder()
                    .commands(commands.toArray(new Class[0]))
                    .create();

            final CommandRuntime<QuarkusCommandInvocation> runtime = AeshCommandRuntimeBuilder
                    .<QuarkusCommandInvocation> builder()
                    .commandRegistry(registry)
                    .commandInvocationProvider(new QuarkusCommandInvocationProvider(quarkusContext))
                    .build();

            final StringBuilder sb = new StringBuilder(registry.getAllCommandNames().iterator().next());
            final String[] args = mainArgsSupplier.getArgs();
            if (args.length > 0) {
                sb.append(" ");
                if (args.length == 1) {
                    sb.append(args[0]);
                } else {
                    for (String arg : args) {
                        if (arg.indexOf(' ') >= 0) {
                            sb.append('"').append(arg).append("\" ");
                        } else {
                            sb.append(arg).append(' ');
                        }
                    }
                }
            }

            runtime.executeCommand(sb.toString());
        }
        //simplified exceptions for now
        catch (CommandNotFoundException | CommandException | CommandLineParserException | CommandValidatorException
                | OptionValidatorException | InterruptedException | IOException | CommandRegistryException e) {
            throw new RuntimeException(e);
        }
    }
}
