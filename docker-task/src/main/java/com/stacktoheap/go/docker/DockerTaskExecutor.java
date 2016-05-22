package com.stacktoheap.go.docker;

import com.stacktoheap.go.docker.Commands.DockerCleanupCommand;
import com.stacktoheap.go.docker.Commands.DockerTaskCommand;
import com.thoughtworks.go.plugin.api.logging.Logger;

public class DockerTaskExecutor {
    Logger logger = Logger.getLoggerFor(DockerTask.class);

    public Result execute(Config config, Context context) {
        try {
            return runCommand(context, config);
        } catch (Exception ex) {
            logger.error("Error running the command", ex);
            return new Result(false, "Failed while running the task", ex);
        }
    }

    private Result runCommand(Context taskContext, Config taskConfig) throws Exception {
        try {
            new DockerTaskCommand(taskContext, taskConfig).run();
            return new Result(true, "Finished");
        } catch(Exception ex) {
            logger.error("Error running the command", ex);
            return new Result(false, "Failed", ex);
        } finally {
            new DockerCleanupCommand(taskContext, taskConfig).run();
        }
    }
}
