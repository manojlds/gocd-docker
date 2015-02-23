package com.stacktoheap.go.docker;

import com.stacktoheap.go.docker.Commands.*;


public class DockerTaskExecutor {
    public Result execute(Config config, Context context) {

        try {
            return runCommand(context, config);
        } catch (Exception e) {
            return new Result(false, "Failed while running the task", e);
        }
    }

    private Result runCommand(Context taskContext, Config taskConfig) throws Exception {
        try {
            new DockerBuildCommand(taskContext, taskConfig).run();
            new DockerTagCommand(taskContext, taskConfig).run();
            new DockerRunCommand(taskContext, taskConfig).run();
            new DockerPushCommand(taskContext, taskConfig).run();

            return new Result(true, "Finished");
        } catch(Exception ex) {
            return new Result(false, "Failed", ex);
        } finally {
            new DockerCleanupCommand(taskContext, taskConfig).run();
        }
    }
}
