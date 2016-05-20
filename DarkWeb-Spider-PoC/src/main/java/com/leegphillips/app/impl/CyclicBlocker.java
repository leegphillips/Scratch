package com.leegphillips.app.impl;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@Component
public class CyclicBlocker implements Predicate<String> {

    private static final Object DUMMY = new Object();

    private Map<String, Object> visited = new HashMap<>();

    @Override
    public boolean test(String url) {
        return visited.putIfAbsent(url, DUMMY) != DUMMY;
    }
}
