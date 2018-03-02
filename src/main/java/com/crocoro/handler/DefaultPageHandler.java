package com.crocoro.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;

public class DefaultPageHandler extends FileHandler {
    public DefaultPageHandler(String fileName) {
        this(new File(fileName));
    }

    public DefaultPageHandler(File file) {
        super(file);
    }

    @Override
    public void handle(HttpExchange http) throws IOException {
        if (http.getRequestURI().getPath().equals("/")) {
            super.handle(http);
        } else {
            http.sendResponseHeaders(404, 0);
            http.close();
        }
    }
}
