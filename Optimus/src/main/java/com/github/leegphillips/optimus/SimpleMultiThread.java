package com.github.leegphillips.optimus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class SimpleMultiThread implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleMultiThread.class);

    private static final ExecutorService POOL = Executors.newCachedThreadPool();

    private static final int QUEUE_SIZE = 262144;
//    private static final int VALUES_SIZE = 4096;
    private static final int VALUES_SIZE = 524288;

    private final BlockingQueue<Integer> input = new ArrayBlockingQueue<>(QUEUE_SIZE);
    private final int[] primes = new int[VALUES_SIZE];

    private int pos = 0;

    private SimpleMultiThread next;

    @Override
    public void run() {
        while (true) {
            try {
                int potential = input.take();
                boolean isPrime = true;
                int pivot = (int) Math.sqrt(potential);
                for (int i = 0; i < pos; i++) {
                    if (primes[i] > pivot) {
                        break;
                    }
                    if (potential % primes[i] == 0) {
                        isPrime = false;
                        break;
                    }
                }
                if (isPrime) {
                    if (pos < primes.length) {
                        primes[pos] = potential;
                        pos++;
                        LOG.debug(Integer.toString(potential));
                    } else {
                        if (next == null) {
                            next = new SimpleMultiThread();
                            POOL.execute(next);
                        }
                        next.add(potential);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    private void add(int potential) {
        try {
            input.put(potential);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
//        long start = System.currentTimeMillis();
        SimpleMultiThread sieve = new SimpleMultiThread();
        POOL.execute(sieve);
        IntStream.iterate(3, i -> i += 2).forEach(sieve::add);
    }
}
