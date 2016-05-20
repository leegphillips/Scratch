package com.leegphillips.app;

import com.leegphillips.app.ext.LinkFilter;
import com.leegphillips.app.ext.ResponseViewer;
import com.leegphillips.app.impl.PathCorrector;
import com.leegphillips.app.impl.CyclicBlocker;
import com.leegphillips.app.impl.PathFilter;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Component
public class Engine {
    private static final Logger LOG = LoggerFactory.getLogger(Engine.class);

    private static final String A = "a";
    private static final String HREF = "href";

    @Autowired
    private CompletionService<List<String>> pipe;

    @Autowired
    private PathFilter pathFilter;

    @Autowired
    private CyclicBlocker cyclicBlocker;

    @Autowired
    private PathCorrector pathCorrector;

    @Autowired
    private ApplicationContext context;

    private Collection<LinkFilter> linkFilters;

    private Collection<ResponseViewer> responseViewers;

    @PostConstruct
    public void init() {
        responseViewers = context.getBeansOfType(ResponseViewer.class).values();
        linkFilters = context.getBeansOfType(LinkFilter.class).values();
    }

    public void add(String url) {
        pipe.submit(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                LOG.debug("downloading: " + url);
                try {
                    Connection.Response response = Jsoup.connect(url).ignoreContentType(true).execute();

                    String contentType = response.contentType();
                    responseViewers.stream().filter(viewer -> viewer.acceptsType(contentType)).forEach(viewer -> viewer.view(response));

                    MimeType mimeType = MimeTypes.getDefaultMimeTypes().forName(response.contentType());
                    if (!MediaType.TEXT_HTML.equals(mimeType.getType().getBaseType())) {
                        return emptyList();
                    }

                    List<String> hrefs = response.parse().getElementsByTag(A).stream()
                            .map(el -> el.attr(HREF))
                            .filter(pathFilter)
                            .map(href -> pathCorrector.apply(href, url))
                            .collect(toList());

                    return hrefs.stream().filter(href -> linkFilters.stream().allMatch(filter -> filter.valid(href))).collect(toList());
                } catch (HttpStatusException e) {
                    LOG.error(e.getStatusCode() + " error downloading: " + url, e);
                }
                return emptyList();
            }
        });
    }

    public void consume() {
        while (true) {
            try {
                pipe.take().get().stream().filter(cyclicBlocker).forEach(this::add);
            } catch (InterruptedException e) {
                LOG.trace("", e);
            } catch (ExecutionException e) {
                LOG.trace("", e);
            }
        }
    }
}
