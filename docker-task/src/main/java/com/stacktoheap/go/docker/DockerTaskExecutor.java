package com.stacktoheap.go.docker;

import com.stacktoheap.go.docker.Commands.*;
import com.stacktoheap.go.docker.Commands.BuildCommands.DockerBuildWithTagCommand;
import com.stacktoheap.go.docker.Commands.PushCommands.DockerTagAndPushCommand;
import com.stacktoheap.go.docker.Commands.RunCommands.DockerRunCommand;


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
            new DockerBuildWithTagCommand(taskContext, taskConfig).run();
            new DockerRunCommand(taskContext, taskConfig).run();
            new DockerTagAndPushCommand(taskContext, taskConfig).run();

            return new Result(true, "Finished");
        } catch(Exception ex) {
            return new Result(false, "Failed", ex);
        } finally {
            new DockerCleanupCommand(taskContext, taskConfig).run();
        }
    }
}
