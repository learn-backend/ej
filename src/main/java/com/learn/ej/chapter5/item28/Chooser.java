package com.learn.ej.chapter5.item28;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Chooser<T> {

    private final List<T> arr;


    public Chooser(Collection<T> arr) {
        this.arr = new ArrayList<>(arr);
    }

    public T choose() {
        Random rnd = ThreadLocalRandom.current();
        return arr.get(rnd.nextInt(arr.size()));
    }
}
