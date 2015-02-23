package com.stacktoheap.go.docker.Commands;

import com.stacktoheap.go.docker.Config;
import com.stacktoheap.go.docker.Context;

public class DockerTagCommand extends DockerCommand {

    public DockerTagCommand(Context taskContext, Config taskConfig) {
        super(taskContext, taskConfig);
    }

    @Override
    protected void buildCommand(Context taskContext, Config taskConfig) {
        command.add("tag");
        command.add(getTemporaryImageTag(taskContext));

        String tag = getDockerBuildTag(taskContext, taskConfig);

        command.add(tag);
    }
}

