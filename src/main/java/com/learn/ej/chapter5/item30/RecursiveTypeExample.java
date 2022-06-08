package com.learn.ej.chapter5.item30;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class RecursiveTypeExample {

    public static void main(String[] args) {

        Integer maxValue = max(List.of(1, 2, 3))
            .orElseThrow(IllegalArgumentException::new);

        System.out.println("max = " + maxValue);
    }


    public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
        if (c.isEmpty()) {
            throw new IllegalArgumentException("Collection is empty!");
        }

        Optional<E> result = Optional.empty();
        for (E e : c) {
            if (result.isEmpty() || e.compareTo(result.get()) > 0) {
                result = Optional.of(e);
            }
        }

        return result;
    }

}


