package com.learn.ej.chapter7.item44;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Item44 {

    public static void main(String[] args) {
        // template method pattern
        Map<String, Object> map = new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Entry<String, Object> eldest) {
                return size() > 100;
            }
        };

        map.put("key", 11);
        Object value = map.get("key");
        System.out.println("key = " + value);



        // using functional interface
        Map<String, Object> upgradeMap = new UpgradeLinkedHashMap<>(m -> m.size() > 100);

        upgradeMap.put("key", 11);
        Object updValue = upgradeMap.get("key");
        System.out.println("upgrade value = " + updValue);
    }

}
