package com.leephillipsconsulting.proxy;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EasyListFilter {
    public EasyListFilter() throws IOException {
        CacheConfig cacheConfig = CacheConfig.custom()
                .build();
        CloseableHttpClient client = CachingHttpClientBuilder.create()
                .setCacheConfig(cacheConfig)
                .setHttpCacheStorage(new FileStorageCache(cacheConfig))
                .build();
        CloseableHttpResponse response = client.execute(new HttpGet("https://easylist-downloads.adblockplus.org/easylist.txt"));
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    public static void main(String[] args) throws IOException {
        new EasyListFilter();
    }
}
