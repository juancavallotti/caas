package com.juancavallotti.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.SpringApplication;

public class CaasService {

    private static final Logger logger = LoggerFactory.getLogger(CaasService.class);

    public static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        SpringApplication.run(SpringBeansConfig.class, args);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("CaaS Service Shutdown.");
        }));
    }
}
