/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.utils;

import java.security.SecureRandom;

public final class NanoIdGenerator {
    public static final SecureRandom DEFAULT_NUMBER_GENERATOR = new SecureRandom();
    private static final char[] DEFAULT_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    public static final int DEFAULT_SIZE = 12;

    public static String nextId() {
        return NanoIdGenerator.randomNanoId(12);
    }

    private static String randomNanoId(int size) {
        if (DEFAULT_ALPHABET.length != 0 && DEFAULT_ALPHABET.length < 256) {
            if (size <= 0) {
                throw new IllegalArgumentException("size must be greater than zero.");
            }
            int mask = (2 << (int)Math.floor(Math.log((double)DEFAULT_ALPHABET.length - 1.0) / Math.log(2.0))) - 1;
            int step = (int)Math.ceil(1.6 * (double)mask * (double)size / (double)DEFAULT_ALPHABET.length);
            StringBuilder idBuilder = new StringBuilder();
            block0: while (true) {
                byte[] bytes = new byte[step];
                DEFAULT_NUMBER_GENERATOR.nextBytes(bytes);
                int i = 0;
                while (true) {
                    if (i >= step) continue block0;
                    int alphabetIndex = bytes[i] & mask;
                    if (alphabetIndex < DEFAULT_ALPHABET.length) {
                        idBuilder.append(DEFAULT_ALPHABET[alphabetIndex]);
                        if (idBuilder.length() == size) {
                            return idBuilder.toString();
                        }
                    }
                    ++i;
                }
            }
        }
        throw new IllegalArgumentException("alphabet must contain between 1 and 255 symbols.");
    }

    private NanoIdGenerator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

