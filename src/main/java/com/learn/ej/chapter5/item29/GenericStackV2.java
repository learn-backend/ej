package com.learn.ej.chapter5.item29;

import java.util.EmptyStackException;

public class GenericStackV2<E> {

    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;


    public GenericStackV2() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E o) {
        if (elements.length >= DEFAULT_INITIAL_CAPACITY) {
            throw new ArrayIndexOutOfBoundsException("stack is full");
        }

        elements[size++] = o;
    }

    public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        @SuppressWarnings("unchecked")
        E result = (E) elements[--size];
        elements[size] = null;
        return result;
    }
}
