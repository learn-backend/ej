package com.learn.ej.chapter5.item29;

import java.util.EmptyStackException;

public class ObjectStack {

    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public ObjectStack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object o) {
        if (elements.length >= DEFAULT_INITIAL_CAPACITY) {
            throw new ArrayIndexOutOfBoundsException("stack is full");
        }

        elements[size++] = o;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        Object result = elements[--size];
        elements[size] = null;
        return result;
    }
}
