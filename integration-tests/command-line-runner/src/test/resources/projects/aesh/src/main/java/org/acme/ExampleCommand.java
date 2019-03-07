package org.acme;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import io.quarkus.clrunner.QuarkusCommand;
import io.quarkus.clrunner.QuarkusCommandInvocation;
import org.aesh.command.CommandDefinition;
import org.aesh.command.CommandException;
import org.aesh.command.CommandResult;
import org.aesh.command.option.Arguments;
import org.aesh.command.option.Option;

@CommandDefinition(name = "cap", description = "testing")
public class ExampleCommand implements QuarkusCommand {

    @Option
    private String file;

    @Arguments
    private List<String> arguments;

    @Override
    public CommandResult execute(QuarkusCommandInvocation commandInvocation) throws CommandException, InterruptedException {
        final Path out = Paths.get(file);

        try {
            final Capitalizer capitalizer = commandInvocation.quarkusContext().getBeanContainer().instance(Capitalizer.class);
            Files.write(out, arguments.stream().map(capitalizer::perform).collect(Collectors.toList()), Charset.defaultCharset());
        } catch (IOException e) {
            throw new CommandException(e);
        }

        return CommandResult.SUCCESS;
    }
}
