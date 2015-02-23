package com.stacktoheap.go.docker;

import javax.print.Doc;
import java.util.Map;

public class Config {
    public final boolean isDockerBuild;
    public final String dockerBuildTag;
    public final String dockerFile;
    public final boolean addPipelineLabel;

    public final boolean isDockerRun;
    public final String dockerRunScript;
    public final String dockerRunArguments;

    public final boolean isDockerPush;
    public final String dockerPushUser;
    public final boolean removeAfterPush;

    public Config(Map config) {
        isDockerBuild = getValue(config, DockerTask.IS_DOCKER_BUILD).equals("true");
        dockerBuildTag = getValue(config, DockerTask.DOCKER_BUILD_TAG);
        dockerFile = getValue(config, DockerTask.DOCKERFILE);
        addPipelineLabel = getValue(config, DockerTask.TAG_WITH_PIPELINE_LABEL).equals("true");

        isDockerRun = getValue(config, DockerTask.IS_DOCKER_RUN).equals("true");
        dockerRunScript = getValue(config, DockerTask.DOCKER_RUN_SCRIPT);
        dockerRunArguments = getValue(config, DockerTask.DOCKER_RUN_ARGUMENTS);

        isDockerPush = getValue(config, DockerTask.IS_DOCKER_PUSH).equals("true");
        dockerPushUser = getValue(config, DockerTask.DOCKER_PUSH_USER);
        removeAfterPush = getValue(config, DockerTask.REMOVE_AFTER_PUSH).equals("true");
    }

    private String getValue(Map config, String property) {
        return (String) ((Map) config.get(property)).get("value");
    }
}
