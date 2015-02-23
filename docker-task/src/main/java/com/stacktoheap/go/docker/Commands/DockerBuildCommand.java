package com.stacktoheap.go.docker.Commands;

import com.stacktoheap.go.docker.Config;
import com.stacktoheap.go.docker.Context;
import org.apache.commons.io.FilenameUtils;

public class DockerBuildCommand extends DockerCommand {

    public DockerBuildCommand(Context taskContext, Config taskConfig) {
        super(taskContext, taskConfig);
    }

    @Override
    public void buildCommand(Context taskContext, Config taskConfig) {
        command.add("docker");
        command.add("build");

        command.add("-t");
        command.add(getTemporaryImageTag(taskContext));

        String dockerFilePath = FilenameUtils.concat(taskContext.getWorkingDir(), taskConfig.dockerFile);
        command.add(dockerFilePath);
    }
}

