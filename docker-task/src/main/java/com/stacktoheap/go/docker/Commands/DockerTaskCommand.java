package com.stacktoheap.go.docker.Commands;

import com.stacktoheap.go.docker.Commands.Base.DockerCompositeCommand;
import com.stacktoheap.go.docker.Commands.BuildCommands.DockerBuildWithTagCommand;
import com.stacktoheap.go.docker.Commands.PushCommands.DockerTagAndPushCommand;
import com.stacktoheap.go.docker.Commands.RunCommands.DockerRunCommand;
import com.stacktoheap.go.docker.Config;
import com.stacktoheap.go.docker.Context;

public class DockerTaskCommand extends DockerCompositeCommand {

    public DockerTaskCommand(Context taskContext, Config taskConfig) {
        super(taskContext, taskConfig);
    }

    @Override
    protected void setupCommands(Context taskContext, Config taskConfig) {
        runCommand(new DockerBuildWithTagCommand(taskContext, taskConfig))
                .then(new DockerRunCommand(taskContext, taskConfig))
                .then(new DockerTagAndPushCommand(taskContext, taskConfig));
    }
}
