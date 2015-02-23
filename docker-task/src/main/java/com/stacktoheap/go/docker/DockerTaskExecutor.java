package com.stacktoheap.go.docker;

import com.stacktoheap.go.docker.Commands.*;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import org.apache.commons.lang3.StringUtils;


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
                new DockerBuildCommand(taskContext, taskConfig).run();
            }
            if (taskConfig.isDockerBuild && !StringUtils.isBlank(taskConfig.dockerBuildTag)) {
                new DockerTagCommand(taskContext, taskConfig).run();
            }
            if (taskConfig.isDockerRun) {
                new DockerRunCommand(taskContext, taskConfig).run();
            }
            if (taskConfig.isDockerPush) {
                new DockerPushCommand(taskContext, taskConfig).run();
            }
            return new Result(true, "Finished");
        } catch(Exception ex) {
            return new Result(false, "Failed", ex);
        } finally {
            if(taskConfig.isDockerBuild) {
                new DockerCleanupCommand(taskContext, taskConfig).run();
            }
        }
    }
}
