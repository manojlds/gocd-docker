package com.stacktoheap.go.docker.Commands;

import com.stacktoheap.go.docker.Config;
import com.stacktoheap.go.docker.Context;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.util.ArrayList;
import java.util.List;

public abstract class DockerCommand implements Command {
    protected final boolean isApplicable;
    protected List<String> command = new ArrayList<>();
    protected static JobConsoleLogger logger = JobConsoleLogger.getConsoleLogger();

    public DockerCommand(Context taskContext, Config taskConfig) {
        command.add("docker");
        buildCommand(taskContext, taskConfig);
        isApplicable = shouldRun(taskContext, taskConfig);
    }

    protected abstract void buildCommand(Context taskContext, Config taskConfig);

    protected abstract boolean shouldRun(Context taskContext, Config taskConfig);

    public void run() throws Exception {
        if(!isApplicable) return;

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        logger.readErrorOf(process.getErrorStream());
        logger.readOutputOf(process.getInputStream());

        int exitCode = process.waitFor();
        process.destroy();

        if (exitCode != 0) {
            throw new Exception("Failed while running task");
        }
    }

    protected String getTemporaryImageTag(Context taskContext) {
        return String.format("docker-task:%s", taskContext.getPipelineLabel());
    }

    protected String getDockerBuildTag(Context taskContext, Config taskConfig) {
        return taskConfig.addPipelineLabel ?
                String.format("%s:%s", taskConfig.dockerBuildTag, taskContext.getPipelineLabel()) :
                taskConfig.dockerBuildTag;
    }

    protected String getDockerPushTag(Context taskContext, Config taskConfig) {
        return String.format("%s/%s", taskConfig.dockerPushUser, getDockerBuildTag(taskContext, taskConfig));
    }
}

