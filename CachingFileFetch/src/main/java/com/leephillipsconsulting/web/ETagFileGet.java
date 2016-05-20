package com.leephillipsconsulting.web;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ETagFileGet implements Closeable {

    private static final String ETAG_HEADER_NAME = "ETag";
    private static final String ETAG_FILENAME = "etag";
    private static final String CONTENTS_FILENAME = "contents";

    private final CloseableHttpClient httpclient;
    private final File cache;

    public ETagFileGet() {
        httpclient = HttpClients.createDefault();
        cache = new File(new File(System.getProperty("java.io.tmpdir")), ETagFileGet.class.getName());
        if (!cache.isDirectory()) {
            cache.mkdir();
        }
    }

    public File get(URL url) throws IOException {
        String encode = URLEncoder.encode(url.toString(), StandardCharsets.UTF_8.toString());
        File current = new File(cache, encode);
        File contents = new File(current, CONTENTS_FILENAME);
        Path contentsPath = contents.toPath();

        try (CloseableHttpResponse response = httpclient.execute(new HttpGet(url.toString()))) {
            Header eTag = response.getLastHeader(ETAG_HEADER_NAME);
            if (eTag == null) {
                Files.copy(response.getEntity().getContent(), contentsPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                String eTagValue = eTag.getValue();
                File eTagFile = new File(current, ETAG_FILENAME);
                if (current.isDirectory()) {
                    String cachedEtag = Files.lines(Paths.get(eTagFile.toURI())).findFirst().get();
                    if (eTagValue != cachedEtag) {
                        Files.copy(response.getEntity().getContent(), contentsPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                } else {
                    current.mkdir();
                    try (FileWriter fileWriter = new FileWriter(eTagFile)) {
                        fileWriter.write(eTagValue);
                    }
                    Files.copy(response.getEntity().getContent(), contentsPath);
                }
            }
        }
        return contents;
    }

    public static void main(String[] args) throws IOException {
        try (ETagFileGet getter = new ETagFileGet()) {
            getter.get(new URL("https://easylist-downloads.adblockplus.org/easylist.txt"));
        }
    }

    @Override
    public void close() throws IOException {
        httpclient.close();
    }
}
