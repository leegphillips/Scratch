package com.github.leegphillips.optimus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class Reference {
    private static final Logger LOG = LoggerFactory.getLogger(Reference.class);

    public static final int MAX = 1_000_000;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        IntStream.iterate(3, i -> i + 1)
                .limit(MAX)
                .filter(potential -> IntStream.rangeClosed(2, (int)(Math.sqrt(potential))).allMatch(n -> potential % n != 0))
                .forEach(prime -> LOG.debug(Integer.toString(prime)));
        LOG.debug("Time taken for " + MAX + ": " + ((System.currentTimeMillis() - start) / 1000) + "s");
    }
}
