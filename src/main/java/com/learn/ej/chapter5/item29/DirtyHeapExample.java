package com.learn.ej.chapter5.item29;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class DirtyHeapExample {

    private void doSomthing(List<String>... stringList) {
        List<Integer> intList = Arrays.asList(100);
        Object[] objects = stringList;
        objects[0] = intList; // 힙 오염 발생
        String s = stringList[0].get(0); // ClassCastException

    }

}
