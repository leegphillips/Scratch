package com.leephillipsconsulting.proxy;

import io.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class App
{
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {
        LOG.debug(Arrays.toString(args));
        HttpProxyServer server =
                DefaultHttpProxyServer.bootstrap()
                        .withPort(10001)
                        .withFiltersSource(new HttpFiltersSourceAdapter() {
                            @Override
                            public HttpFilters filterRequest(HttpRequest originalRequest) {
                                return new ListFilter(originalRequest, null);
                            }
                        })
                        .start();
    }
}
