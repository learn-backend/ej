package com.learn.ej.chapter7.item44;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class FunctionalInterfaceExample {

    public static void main(String[] args) {
        UnaryOperator<Integer> unaryOperator = i -> i + 1;
        System.out.println(unaryOperator.apply(1)); // 2

        BinaryOperator<Integer> binaryOperator = Integer::sum;
        System.out.println(binaryOperator.apply(1,2)); // 3

        Predicate<Integer> predicate = i -> i < 10;
        System.out.println(predicate.test(5)); // true

        Function<Integer, String> function = money -> money + "원 입니다.";
        System.out.println(function.apply(1000)); // 1000원 입니다.

        Supplier<Integer> supplier = () -> 10;
        System.out.println(supplier.get()); // 10

        Consumer<String> consumer = s -> System.out.println("s = " + s);
        consumer.accept("hello world!"); // output: s = hello world

        
        ExecutorService executorService = new ForkJoinPool(4);
        executorService.submit(() -> System.out.println("submit"));
        executorService.submit((Callable<Object>) () -> "1");
    }

}
