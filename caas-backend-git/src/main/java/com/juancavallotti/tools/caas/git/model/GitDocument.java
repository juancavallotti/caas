package com.juancavallotti.tools.caas.git.model;

import com.juancavallotti.tools.caas.api.Document;
import com.juancavallotti.tools.caas.git.GitRepositoryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static com.juancavallotti.tools.caas.git.utils.RepositoryUtils.resolveContentType;

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

        String type = null;

        try {
            //we rely on the most extensive content type library.
            type = Files.probeContentType(documentFile.toPath());
        } catch (IOException ex) {
            //this should not happen
            logger.error("Error while trying to probe file's content type");
            type = null;
        }

        if (!StringUtils.isEmpty(type)) {
            return type;
        } else {
            return resolveContentType(documentFile.getName());
        }

    }

    @Override
    public void setType(String type) {
        throw new UnsupportedOperationException("This is immutable");
    }

}
