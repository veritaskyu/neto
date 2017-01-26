package com.veritasware.neto.session.server.util;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.ToString;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by chacker on 2016-09-19.
 */
@ToString
public class ConsistentHashing<T> {

    private final static HashFunction hashFunction = Hashing.md5();
    private final static Charset UTF8 = Charset.forName("UTF-8");
    private final SortedMap<Long, T> circle = Collections.synchronizedSortedMap(new TreeMap<Long, T>());

    public void add(String name, T node, int replica) {
        for (int i = 0; i < replica; i++) {
            circle.put(hashFunction.hashString(name + i, UTF8).asLong(), node);
        }
    }

    public void remove(String name, int replica) {
        for (int i = 0; i < replica; i++) {
            circle.remove(hashFunction.hashString(name + i, UTF8).asLong());
        }
    }

    public T get(String value) {
        long hash = hashFunction.hashString(value, UTF8).asLong();
        if (!circle.containsKey(hash)) {
            SortedMap<Long, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }


}
