package com.stacktoheap.go.docker;

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
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Extension
public class DockerTask implements GoPlugin {

    public static final String IS_DOCKER_BUILD = "IsDockerBuild";
    public static final String DOCKERFILE = "DockerFile";

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
        return null;
    }

    private GoPluginApiResponse handleTaskExecution(GoPluginApiRequest request) {
        return null;
    }

    private GoPluginApiResponse handleGetConfigRequest() {
        HashMap config = new HashMap();

        HashMap isDockerBuild = new HashMap();
        isDockerBuild.put("default-value", "true");
        isDockerBuild.put("required", true);
        config.put(IS_DOCKER_BUILD, isDockerBuild);

        HashMap dockerFilePath = new HashMap();
        dockerFilePath.put("default-value", ".");
        config.put(DOCKERFILE, dockerFilePath);


        return createResponse(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, config);
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
