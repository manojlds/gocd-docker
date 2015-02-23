package com.stacktoheap.go.docker.Commands;

import com.stacktoheap.go.docker.Config;
import com.stacktoheap.go.docker.Context;

public class DockerBuildCleanupCommand extends DockerCommand {

    public DockerBuildCleanupCommand(Context taskContext, Config taskConfig) {
        super(taskContext, taskConfig);
    }

    @Override
    protected void buildCommand(Context taskContext, Config taskConfig) {
        command.add("rmi");
        command.add(getTemporaryImageTag(taskContext));
    }

    @Override
    protected boolean shouldRun(Context taskContext, Config taskConfig) {
        return taskConfig.isDockerBuild;
    }
}

