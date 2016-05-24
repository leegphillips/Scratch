package com.github.leegphillips.optimus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SingleThreaded {
    private static final Logger LOG = LoggerFactory.getLogger(SingleThreaded.class);

    public static void main(String[] args) {
        List<Integer> primes = new ArrayList<>();
        for (int i = 3; i < Integer.MAX_VALUE; i += 2) {
            boolean isPrime = true;
            for (Integer prime : primes) {
                if (i % prime == 0) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime) {
                primes.add(i);
                LOG.debug("Adding new prime: " + i);
            }
        }
    }
}
