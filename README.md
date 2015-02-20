# gocd-docker
Docker build, run and push task plugin for GoCD

[![Build Status](https://snap-ci.com/manojlds/gocd-docker/branch/master/build_image)](https://snap-ci.com/manojlds/gocd-docker/branch/master)

![](/docs/docker-task-config.png)

## Build

We can use a `Dockerfile` to build an image and tag it.

## Run

If needed, we can run a script against the built image.

Use case - build and test the application in the container environment in which it will actually run.

During run, the entire working directory is mounted as volume into the container, and the specified script (along with the optional arguments) is run in the container.

## Publish

If needed, we can push the built image to Dockerhub.

It is possible to optionally choose any one or a combination of these.

## Pending tasks

- [ ] Support private registry
- [ ] More options while running, like setting specific environment variables
