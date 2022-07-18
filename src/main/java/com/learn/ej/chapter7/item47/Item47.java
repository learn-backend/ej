package com.learn.ej.chapter7.item47;

public class Item47 {
    public static void main(String[] args) {
        /* 스트림을 반환하는 메서드
        static Stream<ProcessHandle> allProcesses() {
            return ProcessHandleImpl.children(0);
        }
        */

        // 자바 타입 추론의 한계로 컴파일 안됨
//        for(ProcessHandle ph : ProcessHandle.allProcesses()::iterator) {
//        }

        // ClassCastException 발생한다 (책에서는 작동한다고 나와 있음)
//        for(ProcessHandle ph : (Iterable<ProcessHandle>) ProcessHandle.allProcesses().iterator()) {
//        }
    }
}
