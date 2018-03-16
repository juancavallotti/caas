package com.juancavallotti.tools.caas.api;

import java.io.InputStream;

public class DefaultDocumentData implements DocumentData {

    private Document document;

    private InputStream data;

    public DefaultDocumentData() {
    }

    public DefaultDocumentData(Document document, InputStream data) {
        this.document = document;
        this.data = data;
    }

    @Override
    public Document getDocument() {
        return document;
    }

    @Override
    public InputStream getData() {
        return data;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setData(InputStream inputStream) {
        this.data = inputStream;
    }
}
