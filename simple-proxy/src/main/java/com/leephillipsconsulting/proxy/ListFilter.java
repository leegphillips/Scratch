package com.leephillipsconsulting.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.littleshoot.proxy.HttpFiltersAdapter;

public class ListFilter extends HttpFiltersAdapter {
    public ListFilter(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        super(originalRequest, ctx);
    }

    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
        if (originalRequest.getUri().contains("google-analytics")) {
            return new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
        }

        return super.clientToProxyRequest(httpObject);
    }
}
