# Clone 재정의는 주의해서 진행하라



`Cloneable` 인터페이스는 의도한 목적을 제대로 이루지 못했다.

> 복제해도 되는 클래스임을 명시하는 용도

**Why?**

- clone 메소드가 선언된 곳이 Cloneable이 아닌 Object이다.

- clone 메소드의 접근제어자가 protected이기에, 외부 객체에서 호출할 수 없다.

```java
public interface Cloneable {
}
```

```java
public class Object {
	protected native Object clone() throws CloneNotSupportedException;
}
```



Cloneable 인터페이스는 clone의 동작 방식을 결정한다.

- Cloneable 인터페이스 미구현: clone을 호출하면 CloneNotSupportedException을 발생시킨다.
- Cloneable 인터페이스를 구현: 객체의 필드들을 하나하나 복사한 객체를 반환



```java
public class CloneTarget {

    @Override
    public CloneTarget clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
```



```java
public class CloneMain {

    public static void main(String[] args) {
        CloneTarget cloneTarget = new CloneTarget();

        try {
            cloneTarget.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

}
```



```bash
java.lang.CloneNotSupportedException: com.example.examplesource.ej.item13.CloneTarget
	at java.base/java.lang.Object.clone(Native Method)
	at com.example.examplesource.ej.item13.CloneTarget.clone(CloneTarget.java:7)
	at com.example.examplesource.ej.item13.CloneMain.main(CloneMain.java:9)
```



clone 메소드는 불변 객체의 경우, 걱정해야할 하위 클래스가 없다. 그러나, 가변객체를 참조하는 순간 재앙으로 돌아선다.

> 재앙이라는 이유를 아래 예제로 확인해보자.



```java
@Setter
@Getter
@AllArgsConstructor
public class Student implements Cloneable {

    private Name name;
    private int age;

    @Override
    public Student clone() {
        try {
            return (Student) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new AssertionError();
        }
    }
}
```

​	[AssertionError](https://stackoverflow.com/questions/24863185/what-is-an-assertionerror-in-which-case-should-i-throw-it-from-my-own-code): 에러가 발생해서는 안되는 곳에서 사용 (명백한 프로그래밍 오류)



```java
@Setter
@Getter
@AllArgsConstructor
public class Name {

    private String name;

}
```





```java
package com.example.examplesource.ej.item13;

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
        assertThat(originalStudent).isNotSameAs(cloneStudent); // 즉 복사한 객체와 원본 객체의 주소값이 다르다.
    }

    @Test
    @DisplayName("복사한 객체와 원본 객체가 가지고 있는 가변 객체의 주소값은 같다.")
    void insideMutableNameSameAsTest() {
        assertThat(originalStudent.getName()).isSameAs(cloneStudent.getName()); // 복사 객체와 원본 객체의 내부는 동일한 주소값을 가진다.
    }

    @Test
    @DisplayName("복사 객체 내부의 객체의 값을 바꾸면, 주소값이 같기 때문에, 원본 객체도 영향을 받는다.")
    void insideMutableNameValueSameAsTest() {
        cloneStudent.getName().setName("upgrade-koi");
        assertThat(originalStudent.getName().getName()).isEqualTo("upgrade-koi"); // 즉 주소값이 같기때문에, 값을 변경하면 원본 객체의 값도 변경된다.
    }
}
```



Name이 가지고 있는 주소값은 얕은 복사를 하기에, 동일한 주소값을 가지게 된다.

이를 개선하려면 아래와 같이 수정해야 한다.



```java
package com.example.examplesource.ej.item13;

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
            cloneStudent.name = name.clone(); // name도 cloneable을 구현하여, deepCopy를 수행해준다. (다른 주소값을 바라보게 된다.)

            return cloneStudent;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}


```



```java
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
    public DeepCopyName clone() { // 추가 구현
        try {
            return (DeepCopyName) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }


    }
}
```



```java
package com.example.examplesource.ej.item13;

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
```



다만 이러한 작업은 너무나도 불편하고, clone을 어디까지 재정의 해야하나는 생각이 든다..



Effective Java에서는 clone메서드 방식보다는 `변환 생성자`와 `변환 팩터리`를 사용하기를 권장하고 있다.

- 언어 모순적이고 위험천만한 객체 생성 메커니즘 (생성자를 쓰지 않는 방식)을 피할 수 있다.
- 엉성하게 문서화된 (Cloneable을 구현하고, Object의 clone을 공변타입변환을 이용,,) 규약에 기대지 않는다.
- 정상적인 final 필드 용법과도 충돌하지 않는다.
- 불필요한 Checked Exception을 던지지 않는다.
- 형변환도 필요하지 않다.



**복사 생성자**

```java
public Student(Student student) { ... }
```

 

**복사 팩터리**

```java
public static Student newInstance(Student student) { ... }
```



> Cloneable을 구현해서 복사하기 보다는 복사 생성자 & 변환 팩터리를 사용하자.
> 단 배열만은, clone 메서드 방식이 가장 깔끔한 방식의 예외라고 할 수 있다.