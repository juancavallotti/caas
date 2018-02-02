package com.juancavallotti.tools.caas.git.model;

import com.juancavallotti.tools.caas.api.Document;
import com.juancavallotti.tools.caas.git.GitRepositoryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class GitDocument implements Document {

    private static final Logger logger = LoggerFactory.getLogger(GitRepositoryParser.class);

    private final File documentFile;

    public GitDocument(File documentFile) {
        this.documentFile = documentFile;
    }

    public InputStream readContents() throws IOException {
        return new FileInputStream(documentFile);
    }

    @Override
    public String getKey() {
        return documentFile.getName();
    }

    @Override
    public void setKey(String key) {
        throw new UnsupportedOperationException("This is immutable");
    }

    @Override
    public String getType() {
        int extPost = documentFile.getName().lastIndexOf('.');
        if (extPost < 0) {
            return ModelConventions.defaultContentType;
        }

        String ext = documentFile.getName().substring(extPost);

        logger.debug("File {} has extension {}", documentFile.getName(), ext);

        return ModelConventions.contentTypeMapping.getOrDefault(ext, ModelConventions.defaultContentType);
    }

    @Override
    public void setType(String type) {
        throw new UnsupportedOperationException("This is immutable");
    }
}
