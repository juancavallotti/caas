package com.juancavallotti.tools.caas.mongo.model;

import com.juancavallotti.tools.caas.api.ConfigurationElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MongoConfigProperties extends HashMap<String, String> implements ConfigurationElement.PropertiesType {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfigProperties.class);

    public static ConfigurationElement.PropertiesType fromMap(Map<String, String> properties) {
        MongoConfigProperties ret = new MongoConfigProperties();
        properties.entrySet().forEach(entry -> ret.put(replaceKey(entry.getKey()), entry.getValue()));
        return ret;
    }

    private static String replaceKey(String key) {
        String replacement = key.toString().replaceAll("\\.", "__");
        logger.debug("Replacing key: {} with key: {}", key, replacement);
        return replacement;
    }

    private static String reverseReplace(String key) {
        String replacement = key.toString().replaceAll("__", ".");
        logger.debug("Replacing key: {} with key: {}", key, replacement);
        return replacement;
    }

    public MongoConfigProperties standardize() {
        MongoConfigProperties ret = new MongoConfigProperties();
        entrySet().forEach(entry -> ret.put(reverseReplace(entry.getKey()), entry.getValue()));
        return ret;
    }

    /**
     * This receives a.b.c and stores a__b__c
     * @param key
     * @param value
     */
    public void setStandardizedProperty(String key, String value) {
        put(replaceKey(key), value);
    }

    /**
     * This receives a.b.c as an argument and retrieves a__b__c
     * @param key
     */
    public String getStandardizedProperty(String key) {
        return get(replaceKey(key));
    }

    public Set<String> getStandardizedPropertyNames() {
        return keySet().stream()
                .map(MongoConfigProperties::reverseReplace)
                .collect(Collectors.toSet());
    }

}
