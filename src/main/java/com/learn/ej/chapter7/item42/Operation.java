package com.learn.ej.chapter7.item42;

import java.util.function.DoubleBinaryOperator;

public enum Operation {
    PLUS((x, y) -> x + y),
    MINUS((x, y) -> x - y),
    TIMES((x, y) -> x * y),
    DIVIDE((x, y) -> x / y);

    private final DoubleBinaryOperator op;

    Operation(DoubleBinaryOperator op) {
        this.op = op;
    }

    public double apply(double x, double y) {
        return op.applyAsDouble(x, y);
    }
}
