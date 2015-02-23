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

    private Result runCommand(Context taskContext, Config taskConfig, JobConsoleLogger console) throws Exception {
        try {
            if (taskConfig.isDockerBuild) {
                ProcessBuilder dockerBuild = createDockerBuildCommandWithOptions(taskContext, taskConfig);
                Process dockerBuildProcess = dockerBuild.start();

                runProcess(console, dockerBuildProcess);
            }
            if (taskConfig.isDockerBuild && !StringUtils.isBlank(taskConfig.dockerBuildTag)) {
                ProcessBuilder dockerTag = createDockerTagCommand(taskContext, taskConfig);
                Process dockerTagProcess = dockerTag.start();

                runProcess(console, dockerTagProcess);
            }
            if (taskConfig.isDockerRun) {
                ProcessBuilder dockerRun = createDockerRunCommandWithOptions(taskContext, taskConfig);
                Process dockerRunProcess = dockerRun.start();

                runProcess(console, dockerRunProcess);
            }
            if (taskConfig.isDockerPush) {
                ProcessBuilder dockerPush = createDockerPushCommandWithOptions(taskContext, taskConfig);
                Process dockerPushProcess = dockerPush.start();

                runProcess(console, dockerPushProcess);
            }

            return new Result(true, "Finished");
        } catch(Exception ex) {
            return new Result(false, "Failed", ex);
        } finally {
            if(taskConfig.isDockerBuild) {
                ProcessBuilder cleanup = createCleanupCommand(taskContext, taskConfig);
                Process cleanupProcess = cleanup.start();

                runProcess(console, cleanupProcess);
            }
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

    private ProcessBuilder createDockerTagCommand(Context taskContext, Config taskConfig) {
        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("tag");
        command.add(getTemporaryImageTag(taskContext));

        String tag = getDockerBuildTag(taskContext, taskConfig);

        command.add(tag);

        return new ProcessBuilder(command);
    }

    private ProcessBuilder createCleanupCommand(Context taskContext, Config taskConfig) {
        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("rmi");
        command.add(getTemporaryImageTag(taskContext));

        return new ProcessBuilder(command);
    }

    private ProcessBuilder createDockerPushCommandWithOptions(Context taskContext, Config taskConfig) {

        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("push");

        command.add(String.format("%s/%s", taskConfig.dockerPushUser, getDockerBuildTag(taskContext, taskConfig)));

        return new ProcessBuilder(command);
    }

    private ProcessBuilder createDockerRunCommandWithOptions(Context taskContext, Config taskConfig) {

        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("run");

        command.add("-v");
        command.add(String.format("%s:/build", taskContext.getAbsoluteWorkingDir()));

        command.add(getTemporaryImageTag(taskContext));

        String scriptFilePath = FilenameUtils.concat("/build", taskConfig.dockerRunScript);
        command.add(scriptFilePath);

        if(!StringUtils.isBlank(taskConfig.dockerRunArguments)) {
            Collections.addAll(command, taskConfig.dockerRunArguments.split("[\r\n]+"));
        }

        return new ProcessBuilder(command);
    }

    private ProcessBuilder createDockerBuildCommandWithOptions(Context taskContext, Config taskConfig) {

        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("build");

        command.add("-t");
        command.add(getTemporaryImageTag(taskContext));

        String dockerFilePath = FilenameUtils.concat(taskContext.getWorkingDir(), taskConfig.dockerFile);
        command.add(dockerFilePath);

        return new ProcessBuilder(command);
    }

    private String getDockerBuildTag(Context taskContext, Config taskConfig) {
        return taskConfig.addPipelineLabel ?
                String.format("%s:%s", taskConfig.dockerBuildTag, taskContext.getPipelineLabel()) :
                taskConfig.dockerBuildTag;
    }

    private String getTemporaryImageTag(Context taskContext) {
        return String.format("docker-task:%s", taskContext.getPipelineLabel());
    }
}
