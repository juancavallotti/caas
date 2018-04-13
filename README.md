# Configuration Service Java Implementation

This is a versatile and full-featured java implementation of Configuration Service, or as I like to call it 'CaaS', that stands for 'Configuration as a Service'.

This is a fully functional, full-featured runtime written for the modular 'Java 9' and spring boot! The service provides some extension points to add additional functionality and has out of the box the following features:

* Mongo DB Storage Support.
* Git Storage Support.
* In-Memory Storage Support.

And it also provides:

* Encrypted values on the backed.
* End 2 End Encryption. (Using JCE)

## Running

In order to run the service, the easiest is to get the docker image available on [docker hub](https://hub.docker.com/r/juancavallotti/caas/):

To get the image `$ docker pull juancavallotti/caas`

To run the container: 

```shell
$ docker run -p 8080:8080 -d -v ~/temp/data/:/opt/caas/data -v ~/temp/conf:/opt/caas/conf juancavallotti/caas:latest
```

Where `~/temp/data` and `~/temp/conf` are the data and configuration directories of the caas runtime respectively.

This repo provides a series of sample configurations, please feel free to ask through github issues if you want to use it and need additional information.

## Building it

This tool uses gradle v 4.6 or later, to build should be just enough with running:

```shell
$ gradle clean build
```

On the repository root, the binary distribution will appear in the `build/` directory of the `caas-runtime` project.

## Configuration

There are several configuration options, the most important choice is the backend to be used, by default, the in-memory one will be used (the docker image uses git based one), but please bear in mind that in-memory is just to try out the API (and for me to test the tooling around it) but not for real-life use.

These are the configurations to switch the backends:

```properties

runtime.backend=com.juancavallotti.tools.caas.git.GitConfigurationServiceBackend
runtime.backend=com.juancavallotti.tools.caas.mongo.MongoConfigurationServiceBackend
runtime.backend=com.juancavallotti.tools.caas.mem.MemoryConfigurationServiceBackend

```
