package com.github.leegphillips.fileUtils;

import java.io.*;
import java.util.stream.IntStream;

public class App
{
    private static final int MAX = Integer.MAX_VALUE;

    public static void main( String[] args ) throws IOException, InterruptedException {
//        int[] ints = IntStream.iterate(1, i -> i + 1).limit(MAX).toArray();

        File file = File.createTempFile(Long.toString(System.currentTimeMillis()), ".tmp");

        System.out.println(file.getAbsolutePath());

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
                try (DataOutputStream dos = new DataOutputStream(bufferedOutputStream)) {
                    IntStream.iterate(1, i -> i + 1).limit(MAX).forEach(i -> {
                        try {
                            dos.writeInt(i);
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.exit(-1);
                        }
                    });
//                    for (int i = 0; i < ints.length; i++) {
//                        dos.writeInt(ints[i]);
//                    }
                }
            }
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            try (BufferedInputStream bis = new BufferedInputStream(fis)) {
                try (DataInputStream dis = new DataInputStream(bis)) {
                    try {
                        for (int value = dis.readInt(); true; value = dis.readInt()) {
                            System.out.println(value);
                        }
                    } catch (EOFException e) {
                        // normal behaviour
                    }
                }
            }
        }
    }
}
