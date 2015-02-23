package com.stacktoheap.go.docker.Commands;

import com.stacktoheap.go.docker.Config;
import com.stacktoheap.go.docker.Context;

public class DockerBuildWithTagCommand extends DockerCompositeCommand {

    public DockerBuildWithTagCommand(Context taskContext, Config taskConfig) {
        super(taskContext, taskConfig);
    }

    @Override
    protected void setupCommands(Context taskContext, Config taskConfig) {
        runCommand(new DockerBuildCommand(taskContext, taskConfig)).then(new DockerTagCommand(taskContext, taskConfig));
    }
}
