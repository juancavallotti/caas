package com.juancavallotti.tools.caas.mem.model;

import com.juancavallotti.tools.caas.api.*;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


public class ModelUtils {

    /**
     * Converts the coordinate object to the default pojo.
     * @param original
     * @return
     */
    public static ConfigCoordinate toCoordinate(ConfigCoordinate original) {

        DefaultConfigCoordinate ret = new DefaultConfigCoordinate();
        copy(original, ret);
        return ret;
    }

    public static ConfigCoordinate toCoordinate(String app, String version, String env) {
        DefaultConfigCoordinate coordinate = new DefaultConfigCoordinate();

        coordinate.setApplication(app);
        coordinate.setVersion(version);
        coordinate.setEnvironment(env);

        return coordinate;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ConfigurationElement> T toConfiguration(ConfigurationElement original) {
        DefaultConfigurationElement ret = new DefaultConfigurationElement();
        return copyElement(original, (T) ret);
    }

    public static Document toDocument(String key, String type) {
        DefaultDocument ret = new DefaultDocument();
        ret.setKey(key);
        ret.setType(type);

        return ret;
    }

    public static void addDocument(ConfigurationElement elm, Document doc) {

        if (elm.getDocuments() == null) {
            elm.setDocuments(new LinkedList<>());
        }
        elm.getDocuments().add(doc);
    }

    public static Optional<Document> findDocument(ConfigurationElement elm, String documentName) {

        if (elm.getDocuments() == null) {
            return Optional.empty();
        }

        return elm.getDocuments().stream()
                .filter(document -> Objects.equals(documentName, document.getKey()))
                .findFirst();

    }

    private static <T extends ConfigurationElement> T copyElement(ConfigurationElement source, T target) {

        if (source == null) {
            return null;
        }

        //copy the default things.
        copy(source, target);

        //copy the properties.
        if (source.getProperties() != null) {
            target.setProperties(new DefaultConfigurationElement.DefaultPropertiesType());
            target.getProperties().putAll(source.getProperties());
        }

        if (source.getImports() != null) {
            target.setImports(new LinkedList<>(source.getImports()));
        }

        if (source.getDocuments() != null) {
            target.setDocuments(new LinkedList<>(source.getDocuments()));
        }

        return target;
    }

    private static ConfigCoordinate copy(ConfigCoordinate source, ConfigCoordinate target) {

        if (source == null) {
            return null;
        }

        target.setApplication(source.getApplication());
        target.setEnvironment(source.getEnvironment());
        target.setVersion(source.getVersion());
        return target;
    }
}
