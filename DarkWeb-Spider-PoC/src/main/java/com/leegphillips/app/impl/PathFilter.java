package com.leegphillips.app.impl;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Component
public class PathFilter implements Predicate<String> {

    private List<String> ILLEGAL_STARTS = Arrays.asList("#", "mailto", "javascript:");

    @Override
    public boolean test(String href) {
        if ("" == href) {
            return false;
        }

        String lowerCase = href.toLowerCase();
        return ILLEGAL_STARTS.stream().noneMatch(lowerCase::startsWith);
    }
}
