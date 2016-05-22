package com.stacktoheap.go.docker.Commands;

import com.stacktoheap.go.docker.Commands.Base.DockerCommand;
import com.stacktoheap.go.docker.Config;
import com.stacktoheap.go.docker.Context;
import org.apache.commons.lang3.StringUtils;

public abstract class DockerTagCommand extends DockerCommand {

    public DockerTagCommand(Context taskContext, Config taskConfig) {
        super(taskContext, taskConfig);
    }

    @Override
    protected void buildCommand(Context taskContext, Config taskConfig) {
        command.add("tag");
        command.add(getTemporaryImageTag(taskContext));

        String tag = getTag(taskContext, taskConfig);

        command.add(tag);
    }

    protected abstract String getTag(Context taskContext, Config taskConfig);

    @Override
    protected boolean shouldRun(Context taskContext, Config taskConfig) {
        return taskConfig.isDockerBuild && !StringUtils.isBlank(taskConfig.dockerBuildTag);
    }
}

