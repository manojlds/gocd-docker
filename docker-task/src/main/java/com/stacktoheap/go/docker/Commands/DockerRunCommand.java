package com.stacktoheap.go.docker.Commands;

import com.stacktoheap.go.docker.Config;
import com.stacktoheap.go.docker.Context;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

public class DockerRunCommand extends DockerCommand {

    public DockerRunCommand(Context taskContext, Config taskConfig) {
        super(taskContext, taskConfig);
    }

    @Override
    protected void buildCommand(Context taskContext, Config taskConfig) {
        command.add("run");

        command.add("-v");
        command.add(String.format("%s:/build", taskContext.getAbsoluteWorkingDir()));

        command.add(getTemporaryImageTag(taskContext));

        String scriptFilePath = FilenameUtils.concat("/build", taskConfig.dockerRunScript);
        command.add(scriptFilePath);

        if(!StringUtils.isBlank(taskConfig.dockerRunArguments)) {
            Collections.addAll(command, taskConfig.dockerRunArguments.split("[\r\n]+"));
        }
    }
}
