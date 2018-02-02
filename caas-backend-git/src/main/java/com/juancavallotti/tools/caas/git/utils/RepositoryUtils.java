package com.juancavallotti.tools.caas.git.utils;

import com.juancavallotti.tools.caas.git.model.ModelConventions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryUtils {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryUtils.class);

    public static String resolveContentType(String fileName) {
        int extPost = fileName.lastIndexOf('.');
        if (extPost < 0) {
            return ModelConventions.defaultContentType;
        }

        String ext = fileName.substring(extPost);

        logger.debug("File {} has extension {}", fileName, ext);

        return ModelConventions.contentTypeMapping.getOrDefault(ext, ModelConventions.defaultContentType);
    }
}
