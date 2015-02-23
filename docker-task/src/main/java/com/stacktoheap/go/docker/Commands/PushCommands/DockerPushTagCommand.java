package com.stacktoheap.go.docker.Commands.PushCommands;

import com.stacktoheap.go.docker.Commands.DockerTagCommand;
import com.stacktoheap.go.docker.Config;
import com.stacktoheap.go.docker.Context;

public class DockerPushTagCommand extends DockerTagCommand {

    public DockerPushTagCommand(Context taskContext, Config taskConfig) {
        super(taskContext, taskConfig);
    }

    @Override
    protected String getTag(Context taskContext, Config taskConfig) {
        return getDockerPushTag(taskContext, taskConfig);
    }
}
