package com.stacktoheap.go.docker.Commands.Base;

import com.stacktoheap.go.docker.Config;
import com.stacktoheap.go.docker.Context;

import java.util.ArrayList;
import java.util.List;

public abstract class DockerCompositeCommand implements Command {
    protected List<Command> commands = new ArrayList<>();

    public DockerCompositeCommand(Context taskContext, Config taskConfig) {
        setupCommands(taskContext, taskConfig);
    }

    protected abstract void setupCommands(Context taskContext, Config taskConfig);

    public DockerCompositeCommand addCommand(Command command) {
        commands.add(command);
        return this;
    }

    public DockerCompositeCommand runCommand(Command command) {
        return addCommand(command);
    }

    public DockerCompositeCommand then(Command command) {
        return addCommand(command);
    }

    public void run() throws Exception {
        for (Command command : commands) {
            command.run();
        }
    }
}

