package com.stacktoheap.go.docker;

import java.util.Map;

public class Config {
    public final boolean isDockerBuild;
    public final String dockerBuildTag;
    public final String dockerFile;

    public final boolean isDockerRun;
    public final String dockerRunScript;
    public final String dockerRunArguments;

    public Config(Map config) {
        isDockerBuild = getValue(config, DockerTask.IS_DOCKER_BUILD).equals("true");
        dockerBuildTag = getValue(config, DockerTask.DOCKER_BUILD_TAG);
        dockerFile = getValue(config, DockerTask.DOCKERFILE);

        isDockerRun = getValue(config, DockerTask.IS_DOCKER_RUN).equals("true");
        dockerRunScript = getValue(config, DockerTask.DOCKER_RUN_SCRIPT);
        dockerRunArguments = getValue(config, DockerTask.DOCKER_RUN_ARGUMENTS);
    }

    private String getValue(Map config, String property) {
        return (String) ((Map) config.get(property)).get("value");
    }
}
