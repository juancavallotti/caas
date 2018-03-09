package com.juancavallotti.tools.caas.impl.admin;

import com.juancavallotti.tools.caas.api.ConfigurationServiceResponse;
import com.juancavallotti.tools.caas.api.admin.Admin;
import com.juancavallotti.tools.caas.config.RuntimeConfigProperties;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class AdminImpl implements Admin{

    private static final Logger logger = LoggerFactory.getLogger(AdminImpl.class);

    @Inject
    private RuntimeConfigProperties runtimeConfig;

    @Inject
    private ConfigurationServiceBackend currentBackend;

    @Override
    public ConfigurationServiceResponse getServerInfo() {
        logger.debug("Gathering server info...");
        Map<String, Object> ret = new HashMap<>();

        populateCurrentBackend(ret);
        populateAvailableBackends(ret);

        return ConfigurationServiceResponse.respond200WithApplicationJson(ret);
    }


    private void populateCurrentBackend(Map<String, Object> data) {
        Map<String, Object> backendInfo = new HashMap<>();
        data.put("currentBackend", backendInfo);

        backendInfo.put("class", runtimeConfig.getBackend());
        backendInfo.put("serviceName", currentBackend.printServiceName());
        backendInfo.put("implementsOperations", currentBackend.implementedFunctionality());

    }

    private void populateAvailableBackends(Map<String, Object> data) {

        ServiceLoader<ConfigurationServiceBackend> sl = ServiceLoader.load(ConfigurationServiceBackend.class);

        List<String> backends = sl.stream()
                .map(be -> be.type().getName())
                .collect(Collectors.toList());

        data.put("availableBackends", backends);
    }

}
