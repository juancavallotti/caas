package com.juancavallotti.tools.caas.mongo.model;

import com.juancavallotti.tools.caas.api.ConfigurationElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MongoConfigProperties extends HashMap<String, String> implements ConfigurationElement.PropertiesType {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfigProperties.class);

    public static ConfigurationElement.PropertiesType fromMap(Map<String, String> properties) {
        MongoConfigProperties ret = new MongoConfigProperties();
        properties.entrySet().forEach(entry -> ret.put(replaceKey(entry.getKey()), entry.getValue()));
        return ret;
    }

    private static String replaceKey(String key) {
        String replacement = key.toString().replaceAll("\\.", "__");
        logger.info("Replacing key: {} with key: {}", key, replacement);
        return replacement;
    }

    private String reverseReplace(String key) {
        String replacement = key.toString().replaceAll("__", ".");
        logger.info("Replacing key: {} with key: {}", key, replacement);
        return replacement;
    }

    public MongoConfigProperties standardize() {
        MongoConfigProperties ret = new MongoConfigProperties();
        entrySet().forEach(entry -> ret.put(reverseReplace(entry.getKey()), entry.getValue()));
        return ret;
    }

}
