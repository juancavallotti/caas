# Configuration as a Service
# Version: 0.1
# Based on: openjdk:9
FROM openjdk:9-slim
MAINTAINER Juan Alberto Lopez Cavallotti <juancavallotti@gmail.com>
# CaaS Service:
ENV CAAS_RUNTIME_VERSION=1.0-SNAPSHOT
COPY ./caas-runtime/build/distributions/caas-runtime-$CAAS_RUNTIME_VERSION.tar /opt/
WORKDIR /opt
RUN tar xf /opt/caas-runtime-$CAAS_RUNTIME_VERSION.tar
RUN rm -f /opt/caas-runtime-$CAAS_RUNTIME_VERSION.tar
RUN mv caas-runtime-$CAAS_RUNTIME_VERSION caas
WORKDIR /opt/caas
#the mountpoint of data for the modules.
RUN mkdir data
COPY ./caas-docker/config.properties /opt/caas/conf/config.properties
# Define mount points
VOLUME ["/opt/caas/data"]
# Configure external access:
# Expose HTTP Endpoint
EXPOSE 8080
# Environment and execution:
WORKDIR /opt/caas
CMD ["/opt/caas/bin/caas-runtime"]