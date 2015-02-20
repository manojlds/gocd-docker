package com.stacktoheap.go.docker;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DockerTaskExecutor {
    public Result execute(Config config, Context context, JobConsoleLogger console) {

        try {
            return runCommand(context, config, console);
        } catch (Exception e) {
            return new Result(false, "Failed while running the task", e);
        }
    }

    private Result runCommand(Context taskContext, Config taskConfig, JobConsoleLogger console) throws IOException, InterruptedException {

        if(taskConfig.isDockerBuild) {
            ProcessBuilder dockerBuild = createDockerBuildCommandWithOptions(taskContext, taskConfig);
            Process dockerBuildProcess = dockerBuild.start();

            return runProcess(console, dockerBuildProcess, "Built image successfully");
        } else {
            return null;
        }
    }

    private Result runProcess(JobConsoleLogger console, Process process, String successMessage) throws InterruptedException {
        console.readErrorOf(process.getErrorStream());
        console.readOutputOf(process.getInputStream());

        int exitCode = process.waitFor();
        process.destroy();

        if (exitCode != 0) {
            return new Result(false, "Failure while running task");
        }

        return new Result(true, successMessage);
    }

    ProcessBuilder createDockerBuildCommandWithOptions(Context taskContext, Config taskConfig) {

        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("build");

        if(!StringUtils.isBlank(taskConfig.dockerBuildTag)) {
            command.add("-t");
            command.add(taskConfig.dockerBuildTag);
        }
        String dockerFilePath = FilenameUtils.concat(taskContext.getWorkingDir(), taskConfig.dockerFile);

        command.add(dockerFilePath);

        return new ProcessBuilder(command);
    }
}
