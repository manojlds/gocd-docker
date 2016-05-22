package com.stacktoheap.go.docker.Commands.PushCommands;

import com.stacktoheap.go.docker.Commands.Base.DockerCommand;
import com.stacktoheap.go.docker.Config;
import com.stacktoheap.go.docker.Context;

public class DockerPushCommand extends DockerCommand {

    public DockerPushCommand(Context taskContext, Config taskConfig) {
        super(taskContext, taskConfig);
    }

    @Override
    protected void buildCommand(Context taskContext, Config taskConfig) {
        command.add("push");

        command.add(getDockerPushTag(taskContext, taskConfig));
    }

    @Override
    protected boolean shouldRun(Context taskContext, Config taskConfig) {
        return taskConfig.isDockerPush;
    }
}

