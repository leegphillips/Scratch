package com.github.leegphillips.optimus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;

public class DiskBased {

    //TODO this isnt finished and needs a refactor

    private static final Logger LOG = LoggerFactory.getLogger(DiskBased.class);

    public static void main(String args[]) throws IOException {
        Path tempDirectory = Files.createTempDirectory(DiskBased.class.getSimpleName());
        if (!tempDirectory.toFile().isDirectory())
            tempDirectory.toFile().mkdir();

        LOG.debug("Location: " + tempDirectory.toFile().getAbsolutePath());

        File master = Files.createTempFile(tempDirectory, "main", "suffix").toFile();

        try (FileOutputStream fileOutputStream = new FileOutputStream(master)) {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
                try (DataOutputStream dos = new DataOutputStream(bufferedOutputStream)) {
                    dos.writeInt(2);
                    dos.writeInt(2);
                    IntStream.range(3, Integer.MAX_VALUE)
                            .filter(i -> i % 2 != 0)
                            .forEach(potential -> {
                                try {
                                    dos.writeInt(potential);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                }
            }
        }

        LOG.debug(Long.toString(master.length()));

        int toProcess = new DiskBased().nextToProcess(master);

        File next = Files.createTempFile(tempDirectory, "next", "suffix").toFile();
        try (FileInputStream fileInputStream = new FileInputStream(master)) {
            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
                try (DataInputStream dis = new DataInputStream(bufferedInputStream)) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(next)) {
                        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
                            try (DataOutputStream dos = new DataOutputStream(bufferedOutputStream)) {
                                dos.writeInt(toProcess);
                                int ignore = dis.readInt();
                                try {
                                    while (true) {
                                        int value = dis.readInt();
                                        if (value <= toProcess || value % toProcess != 0) {
                                            dos.writeInt(value);
                                        }
                                    }
                                } catch (EOFException e) {
                                    // legal
                                }
                            }
                        }
                    }
                }
            }
        }

        LOG.debug(Long.toString(next.length()));
    }

    private int nextToProcess(File current) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(current)) {
            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
                try (DataInputStream dis = new DataInputStream(bufferedInputStream)) {
                    int lastProcessed = dis.readInt();
                    for (int i = 0; true; i++) {
                        if (lastProcessed == dis.readInt())
                            return dis.readInt();
                    }
                }
            }
        }
    }
}
