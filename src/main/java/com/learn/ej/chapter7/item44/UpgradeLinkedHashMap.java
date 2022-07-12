package com.learn.ej.chapter7.item44;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

public class UpgradeLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    private final Predicate<Map<K, V>> eldestPredicate;

    public UpgradeLinkedHashMap() {
        this(m -> false);
    }

    public UpgradeLinkedHashMap(Predicate<Map<K, V>> eldestPredicate) {
        this.eldestPredicate = eldestPredicate;
    }

    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest) {
        return eldestPredicate.test(this);
    }
}
