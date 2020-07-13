package com.yimao.cloud.base.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2018-12-26 16:12:52
 **/
@Slf4j
public class RandomUtil {
    private static final Random RANDOM = new Random();

    public static int nextInt(final int startInclusive, final int endExclusive) {
        if (endExclusive < startInclusive) {
            log.error("Start value must be smaller or equal to end value.");
        }
        if (startInclusive < 0) {
            log.error("Both range values must be non-negative.");
        }
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + RANDOM.nextInt(endExclusive - startInclusive);
    }
}
