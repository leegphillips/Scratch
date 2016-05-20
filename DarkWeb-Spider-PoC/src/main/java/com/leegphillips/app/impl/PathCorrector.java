package com.leegphillips.app.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@Component
public class PathCorrector implements BiFunction<String, String, String> {
    private static final Logger LOG = LoggerFactory.getLogger(PathCorrector.class);

    private static final String FORWARD_SLASH = "/";
    private static final String QUERY = "?";

    private static final List<String> PROTOCOLS = Arrays.asList("http://", "https://");

    @Override
    public String apply(String href, String parent) {
        if (PROTOCOLS.stream().anyMatch(protocol -> href.startsWith(protocol))) {
            return href;
        }

        if (href.startsWith(FORWARD_SLASH)) {
            try {
                URL url = new URL(parent);
                String path = url.getPath();
                if (FORWARD_SLASH.equals(path)) {
                    return concat(href, parent);
                }

                return replaceSection(href, parent, path);
            } catch (MalformedURLException e) {
                LOG.trace(parent + " could not be encoded into a URL", e);
            }
        }

        if (href.startsWith(QUERY)) {
            if (parent.contains(QUERY)) {
                return parent.substring(0, parent.indexOf(QUERY)) + href;
            }
            return concat(href, parent);
        }

        return concat(href, parent);
    }

    private String replaceSection(String href, String parent, String replace) {
        return parent.substring(0, parent.lastIndexOf(replace)) + href;
    }

    private String concat(String href, String parent) {
        String newPath = parent.endsWith(FORWARD_SLASH)
                || href.startsWith(FORWARD_SLASH)
                || href.startsWith(QUERY) ? parent + href : parent + FORWARD_SLASH + href;
        LOG.debug("Found: " + href + ", correcting to: " + newPath);
        return newPath;
    }
}
