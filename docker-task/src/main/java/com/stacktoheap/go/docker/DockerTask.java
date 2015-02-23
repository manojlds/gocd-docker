package com.stacktoheap.go.docker;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Extension
public class DockerTask implements GoPlugin {

    public static final String IS_DOCKER_BUILD = "IsDockerBuild";
    public static final String DOCKER_BUILD_TAG = "DockerBuildTag";
    public static final String DOCKERFILE = "DockerFile";
    public static final String TAG_WITH_PIPELINE_LABEL = "TagWithPipelineLabel";

    public static final String IS_DOCKER_RUN = "IsDockerRun";
    public static final String DOCKER_RUN_SCRIPT = "DockerRunScript";
    public static final String DOCKER_RUN_ARGUMENTS = "DockerRunArguments";

    public static final String IS_DOCKER_PUSH = "IsDockerPush";
    public static final String DOCKER_PUSH_USER = "DockerPushUser";
    public static final String REMOVE_AFTER_PUSH = "RemoveAfterPush";

    Logger logger = Logger.getLoggerFor(DockerTask.class);

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {

    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier("task", Arrays.asList("1.0"));
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
        if ("configuration".equals(request.requestName())) {
            return handleGetConfigRequest();
        } else if ("validate".equals(request.requestName())) {
            return handleValidation(request);
        } else if ("execute".equals(request.requestName())) {
            return handleTaskExecution(request);
        } else if ("view".equals(request.requestName())) {
            return handleTaskView();
        }
        throw new UnhandledRequestTypeException(request.requestName());
    }

    private GoPluginApiResponse handleValidation(GoPluginApiRequest request) {
        HashMap validationResult = new HashMap();
        int responseCode = DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE;
        Map configMap = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);

        boolean dockerBuildStatus = validateDockerBuild(validationResult, configMap);

        boolean dockerRunStatus = validateDockerRun(validationResult, configMap);

        boolean dockerPushStatus = validateDockerPush(validationResult, configMap);

        if(!dockerBuildStatus || !dockerRunStatus || !dockerPushStatus)
            responseCode = DefaultGoPluginApiResponse.VALIDATION_FAILED;

        return createResponse(responseCode, validationResult);
    }

    private boolean validateDockerPush(HashMap validationResult, Map configMap) {
        if(((Map)configMap.get(IS_DOCKER_PUSH)).get("value").equals("true")) {
            String dockerPushUser = (String) ((Map)configMap.get(DOCKER_PUSH_USER)).get("value");
            if(StringUtils.isBlank(dockerPushUser)) {
                HashMap errorMap = new HashMap();
                errorMap.put(DOCKER_PUSH_USER, "Docker hub user must be specified");
                validationResult.put("errors", errorMap);
                return false;
            }

        }
        return true;
    }

    private boolean validateDockerRun(HashMap validationResult, Map configMap) {
        if(((Map)configMap.get(IS_DOCKER_RUN)).get("value").equals("true")) {
            String runScript = (String) ((Map)configMap.get(DOCKER_RUN_SCRIPT)).get("value");
            if(StringUtils.isBlank(runScript)) {
                HashMap errorMap = new HashMap();
                errorMap.put(DOCKER_RUN_SCRIPT, "Script to be run must be specified");
                validationResult.put("errors", errorMap);
                return false;
            }

        }
        return true;
    }

    private boolean validateDockerBuild(HashMap validationResult, Map configMap) {
        if(((Map)configMap.get(IS_DOCKER_BUILD)).get("value").equals("true")) {
            String dockerFile = (String) ((Map)configMap.get(DOCKERFILE)).get("value");
            if(StringUtils.isBlank(dockerFile)) {
                HashMap errorMap = new HashMap();
                errorMap.put(DOCKERFILE, "Dockerfile path needs to be specified");
                validationResult.put("errors", errorMap);
                return false;
            }

        }
        return true;
    }

    private GoPluginApiResponse handleTaskExecution(GoPluginApiRequest request) {
        DockerTaskExecutor executor = new DockerTaskExecutor();
        Map executionRequest = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);
        Map config = (Map) executionRequest.get("config");
        Map context = (Map) executionRequest.get("context");

        Result result = executor.execute(new Config(config), new Context(context));
        return createResponse(result.responseCode(), result.toMap());
    }

    private GoPluginApiResponse handleGetConfigRequest() {
        HashMap config = new HashMap();
        addDockerBuildConfig(config);
        addDockerRunConfig(config);
        addDockerPushConfig(config);

        return createResponse(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, config);
    }

    private void addDockerPushConfig(HashMap config) {
        HashMap isDockerPush = new HashMap();
        isDockerPush.put("default-value", "false");
        isDockerPush.put("required", true);
        config.put(IS_DOCKER_PUSH, isDockerPush);

        HashMap dockerPushUser = new HashMap();
        dockerPushUser.put("default-value", "");
        dockerPushUser.put("required", false);
        config.put(DOCKER_PUSH_USER, dockerPushUser);

        HashMap removeAfterPush = new HashMap();
        removeAfterPush.put("default-value", "true");
        removeAfterPush.put("required", true);
        config.put(REMOVE_AFTER_PUSH, removeAfterPush);
    }

    private void addDockerRunConfig(HashMap config) {
        HashMap isDockerRun = new HashMap();
        isDockerRun.put("default-value", "false");
        isDockerRun.put("required", true);
        config.put(IS_DOCKER_RUN, isDockerRun);

        HashMap dockerRunScript = new HashMap();
        dockerRunScript.put("default-value", "");
        dockerRunScript.put("required", false);
        config.put(DOCKER_RUN_SCRIPT, dockerRunScript);

        HashMap dockerRunArguments = new HashMap();
        dockerRunArguments.put("required", false);
        config.put(DOCKER_RUN_ARGUMENTS, dockerRunArguments);
    }

    private void addDockerBuildConfig(HashMap config) {
        HashMap isDockerBuild = new HashMap();
        isDockerBuild.put("default-value", "true");
        isDockerBuild.put("required", true);
        config.put(IS_DOCKER_BUILD, isDockerBuild);

        HashMap dockerFilePath = new HashMap();
        dockerFilePath.put("default-value", ".");
        dockerFilePath.put("required", false);
        config.put(DOCKERFILE, dockerFilePath);

        HashMap dockerBuildTag = new HashMap();
        dockerBuildTag.put("required", false);
        config.put(DOCKER_BUILD_TAG, dockerBuildTag);

        HashMap addPipelineLabel = new HashMap();
        addPipelineLabel.put("default-value", "true");
        addPipelineLabel.put("required", true);
        config.put(TAG_WITH_PIPELINE_LABEL, addPipelineLabel);
    }

    private GoPluginApiResponse handleTaskView() {
        int responseCode = DefaultGoApiResponse.SUCCESS_RESPONSE_CODE;
        Map view = new HashMap();
        view.put("displayValue", "Docker Task");
        try {
            view.put("template", IOUtils.toString(getClass().getResourceAsStream("/views/task.template.html"), "UTF-8"));
        } catch (Exception e) {
            responseCode = DefaultGoApiResponse.INTERNAL_ERROR;
            String errorMessage = "Failed to find template: " + e.getMessage();
            view.put("exception", errorMessage);
            logger.error(errorMessage, e);
        }
        return createResponse(responseCode, view);
    }

    private GoPluginApiResponse createResponse(int responseCode, Map body) {
        final DefaultGoPluginApiResponse response = new DefaultGoPluginApiResponse(responseCode);
        response.setResponseBody(new GsonBuilder().serializeNulls().create().toJson(body));
        return response;
    }
}
