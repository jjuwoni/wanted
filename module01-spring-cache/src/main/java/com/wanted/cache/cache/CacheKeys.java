package com.wanted.cache.cache;

import java.util.Locale;

public final class CacheKeys {

    /* comment.
    *   캐시는 Key 가 존재하며, Key 가 같으면
    *   같은 캐시 항목으로 취급되게 된다.
    * */
    private CacheKeys() {
    }

    /* comment.
    *   ex) popular::food::2000::12000 -> 이렇게 1개의 key를 생성한다.
    *   ex) popular::food::2000::10000 -> 이렇게 1개의 key를 생성한다.
    * */
    public static String search(String keyword, String category, Integer minPrice, Integer maxPrice) {
        return normalize(keyword) + "::" + normalize(category) + "::" + value(minPrice) + "::" + value(maxPrice);
    }


    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "*";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private static String value(Integer value) {
        return value == null ? "*" : String.valueOf(value);
    }
}
