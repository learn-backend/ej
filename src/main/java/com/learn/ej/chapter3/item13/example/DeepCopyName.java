package com.example.examplesource.ej.item13;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class DeepCopyName implements Cloneable {

    private String name;

    @Override
    public DeepCopyName clone() {
        try {
            return (DeepCopyName) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }


    }
}