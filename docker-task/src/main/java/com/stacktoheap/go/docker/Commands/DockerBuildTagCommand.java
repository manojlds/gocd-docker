package com.stacktoheap.go.docker.Commands;

import com.stacktoheap.go.docker.Config;
import com.stacktoheap.go.docker.Context;

public class DockerBuildTagCommand extends DockerTagCommand {

    public DockerBuildTagCommand(Context taskContext, Config taskConfig) {
        super(taskContext, taskConfig);
    }

    @Override
    protected String getTag(Context taskContext, Config taskConfig) {
        return getDockerBuildTag(taskContext, taskConfig);
    }
}

