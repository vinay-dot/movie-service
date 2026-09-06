package com.vinsguru.netflux.movie.util;

import java.util.Objects;

public class LimitResolver {

    public static final int DEFAULT_LIMIT = 20;
    public static final int MAX_LIMIT = 100;

    private LimitResolver() {
    }

    public static int resolve(Integer requested) {
        if (Objects.isNull(requested) || requested < 1) {
            return DEFAULT_LIMIT;
        }
        return Math.min(requested, MAX_LIMIT);
    }

}
