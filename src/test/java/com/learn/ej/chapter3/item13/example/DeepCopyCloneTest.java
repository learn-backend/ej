package com.learn.ej.chapter3.item13.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DeepCopyCloneTest {

    private DeepCopyStudent originalStudent;
    private DeepCopyStudent cloneStudent;

    @BeforeEach
    void init() {
        originalStudent = new DeepCopyStudent(new DeepCopyName("koi"), 10);
        cloneStudent = originalStudent.clone();
    }

    @Test
    @DisplayName("복사한 객체와 원본 객체의 주소값은 다르다.")
    void mutableStudentTest() {
        assertThat(originalStudent).isNotSameAs(cloneStudent); // 복사한 주소값 객체는

    }

    @Test
    @DisplayName("복사한 객체와 원본 객체가 가지고 있는 가변 객체의 주소값은 다르다.")
    void insideMutableNameNotSameAsTest() {
        assertThat(originalStudent.getName()).isNotSameAs(cloneStudent.getName()); // isSameAs -> isNotSameAs
    }

    @Test
    @DisplayName("복사 객체 내부의 객체의 값을 바꾸면, 주소값이 다르기에, 원본 객체는 영향을 받지 않는다.")
    void insideMutableNameValueSameAsTest() {
        cloneStudent.getName().setName("upgrade-koi");
        assertThat(originalStudent.getName().getName()).isEqualTo("koi"); // upgrade-koi -> koi
    }
}