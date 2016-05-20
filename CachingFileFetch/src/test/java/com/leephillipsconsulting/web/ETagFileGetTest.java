package com.leephillipsconsulting.web;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ETagFileGetTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    private static ETagFileGet eTagFileGet;

    @BeforeClass
    public static void beforeClass() {
        eTagFileGet = new ETagFileGet();
    }

    @AfterClass
    public static void afterClass() throws IOException {
        eTagFileGet.close();
    }

    @Test
    public void initialFetch() throws IOException {
        stubFor(get(urlEqualTo("/my/resource.txt"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("ETag", "randomvalue")
                        .withBody("randombody")));

        File file = eTagFileGet.get(new URL("http://localhost:8089/my/resource.txt"));

        System.out.println();
    }
}
