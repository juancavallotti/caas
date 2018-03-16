package com.juancavallotti.tools.caas.api;

import java.io.InputStream;

public class DefaultDocumentData implements DocumentData {

    private Document document;

    private InputStream inputStream;

    public DefaultDocumentData() {
    }

    public DefaultDocumentData(Document document, InputStream inputStream) {
        this.document = document;
        this.inputStream = inputStream;
    }

    @Override
    public Document getDocument() {
        return null;
    }

    @Override
    public InputStream getData() {
        return null;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
