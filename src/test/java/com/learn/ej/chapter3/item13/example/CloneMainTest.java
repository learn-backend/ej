package com.learn.ej.chapter3.item13.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CloneMainTest {

    private Student originalStudent;
    private Student cloneStudent;

    @BeforeEach
    void init() {
        originalStudent = new Student(new Name("koi"), 10);
        cloneStudent = originalStudent.clone();
    }

    @Test
    @DisplayName("복사한 객체와 원본 객체의 주소값은 다르다.")
    void mutableStudentTest() {
        assertThat(originalStudent).isNotSameAs(cloneStudent); // 복사한 주소값 객체는


    }

    @Test
    @DisplayName("복사한 객체와 원본 객체가 가지고 있는 가변 객체의 주소값은 같다.")
    void insideMutableNameSameAsTest() {
        assertThat(originalStudent.getName()).isSameAs(cloneStudent.getName()); // success
    }

    @Test
    @DisplayName("복사 객체 내부의 객체의 값을 바꾸면, 주소값이 같기 때문에, 원본 객체도 영향을 받는다.")
    void insideMutableNameValueSameAsTest() {
        cloneStudent.getName().setName("upgrade-koi");
        assertThat(originalStudent.getName().getName()).isEqualTo("upgrade-koi"); // ???
    }
}