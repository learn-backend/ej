package com.learn.ej.chapter11.item79.example;

@FunctionalInterface
public interface SetObserver<E> {

    void added(ObservableSet<E> set, E element);
}
