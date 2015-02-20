package com.stacktoheap.go.docker;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

        try {
            if (taskConfig.isDockerBuild) {
                ProcessBuilder dockerBuild = createDockerBuildCommandWithOptions(taskContext, taskConfig);
                Process dockerBuildProcess = dockerBuild.start();

                runProcess(console, dockerBuildProcess);
            }
            if (taskConfig.isDockerRun) {
                ProcessBuilder dockerRun = createDockerRunCommandWithOptions(taskContext, taskConfig);
                Process dockerRunProcess = dockerRun.start();

                runProcess(console, dockerRunProcess);
            }

            return new Result(true, "Finished");
        } catch(Exception ex) {
            return new Result(false, "Failed", ex);
        }
    }

    private void runProcess(JobConsoleLogger console, Process process) throws Exception {
        console.readErrorOf(process.getErrorStream());
        console.readOutputOf(process.getInputStream());

        int exitCode = process.waitFor();
        process.destroy();

        if (exitCode != 0) {
            throw new Exception("Failed while running task");
        }
    }

    ProcessBuilder createDockerRunCommandWithOptions(Context taskContext, Config taskConfig) {

        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("run");

        command.add("-v");
        command.add(String.format("%s:/build", taskContext.getAbsoluteWorkingDir()));

        command.add(taskConfig.dockerBuildTag);

        String scriptFilePath = FilenameUtils.concat("/build", taskConfig.dockerRunScript);
        command.add(scriptFilePath);

        if(!StringUtils.isBlank(taskConfig.dockerRunArguments)) {
            Collections.addAll(command, taskConfig.dockerRunArguments.split("[\r\n]+"));
        }

        return new ProcessBuilder(command);
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
