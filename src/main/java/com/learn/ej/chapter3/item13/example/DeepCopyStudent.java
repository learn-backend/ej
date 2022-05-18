package com.learn.ej.chapter3.item13.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class DeepCopyStudent implements Cloneable {

    private DeepCopyName name;
    private int age;

    @Override
    public DeepCopyStudent clone() {
        try {
            DeepCopyStudent cloneStudent = (DeepCopyStudent) super.clone();
            cloneStudent.name = name.clone();

            return cloneStudent;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

