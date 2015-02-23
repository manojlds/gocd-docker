package com.stacktoheap.go.docker.Commands.PushCommands;

import com.stacktoheap.go.docker.Commands.DockerCompositeCommand;
import com.stacktoheap.go.docker.Config;
import com.stacktoheap.go.docker.Context;

public class DockerTagAndPushCommand extends DockerCompositeCommand {

    public DockerTagAndPushCommand(Context taskContext, Config taskConfig) {
        super(taskContext, taskConfig);
    }

    @Override
    protected void setupCommands(Context taskContext, Config taskConfig) {
        runCommand(new DockerPushTagCommand(taskContext, taskConfig)).then(new DockerPushCommand(taskContext, taskConfig));
    }
}
