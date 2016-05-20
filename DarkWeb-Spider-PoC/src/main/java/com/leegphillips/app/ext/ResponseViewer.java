package com.leegphillips.app.ext;

import org.jsoup.Connection;

public interface ResponseViewer {
    boolean acceptsType(String mimeType);
    void view(Connection.Response response);
}
