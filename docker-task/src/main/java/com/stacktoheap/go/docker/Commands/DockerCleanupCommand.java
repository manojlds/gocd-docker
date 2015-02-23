package com.stacktoheap.go.docker.Commands;

import com.stacktoheap.go.docker.Config;
import com.stacktoheap.go.docker.Context;

public class DockerCleanupCommand extends DockerCompositeCommand {

    public DockerCleanupCommand(Context taskContext, Config taskConfig) {
        super(taskContext, taskConfig);
    }

    @Override
    protected void setupCommands(Context taskContext, Config taskConfig) {
        runCommand(new DockerBuildCleanupCommand(taskContext, taskConfig))
                .then(new DockerPushCleanupCommand(taskContext, taskConfig));
    }
}
