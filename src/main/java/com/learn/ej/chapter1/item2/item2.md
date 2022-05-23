# 생성자에 매개변수가 많다면 빌더를 고려하라

## 장점

- 매개변수가 많아지면, 클라이언트 코드 가독성이 떨어진다.
- 매개변수의 순서를 바꿔 건네줘도, 컴파일러는 알아채지 못하고 버그로 이어진다.
- (개인) 클래스에 필드가 추가되고, 생성자에 추가를 해줘야 한다면 클라이언트 코드에, 영향을 끼친다.
  - 의견: 점층적 생성자 패턴으로 개선이 가능해서, 안적혀 있는듯 하다.
- 객체 생성시까지 일관성(Consistency)가 유지된다.
  - 자바빈즈 패턴으로는 일관성이 깨지고 불변을 생성할 수 없다. (자바빈즈: 기본생성자, 게터, 세터)
  - 매개변수가 유효한지 생성자에서 확인할 수 없다. (별도의 Validation 메소드를 생성하거나 호출해야함)

## 단점

- 빌더 생성 비용이 크지는 않지만, 객체를 새로 생성하므로 성능에 민감한 상황에서는 문제가 될 수 있다.
- 4개 이상은 되어야 값어치를 한다.
  - 의견: 단 API가 변경가능성이 있고, 해당 클래스를 사용하는곳이 많다면, Builder 패턴도 나쁜 선택은 아니라고 생각한다.

## 모르는 용어

- 점층적 생성자 패턴, 자바빈즈 패턴
  - 점층적 생성자 패턴: 필요한 인자수만큼 생성자를 만들고  `this(a, b, c, ..)` 키워드를 통해 전달
  - 자바빈즈 패턴: 빈 생성자 & Getter,Setter 가 존재하는 클래스를 의미

플루언트 API & 메소드 연쇄

- 메소드 체이닝 ex) `Assertions.assertThat().isEqualTo()`

재귀적 타입 한정

공변 반환 타이핑

- JDK 5부터 지원 & SubClass 타입으로 오버라이딩이 가능함

```java
public class A {

    public A get() {
        return this;
    }

    public void printClassName() {
        System.out.println("A class!!");
    }
}

public class B extends A {

    @Override
    public B get() {
        return this;
    }

    public void printClassName() {
        System.out.println("B class!!");
    }
}

public class Main {

    public static void main(String[] args) {
        A a = new A();

        a.get().printClassName(); // A class!!

        A b = new B();
        b.get().printClassName(); // B class!!
    }
}
```