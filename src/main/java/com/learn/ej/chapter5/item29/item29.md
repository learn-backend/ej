# 이왕이면 제네릭 타입으로 만들라



제네릭 타입을 사용하는 것은 일반적으로 쉽지만 새로 만드는 일은 조금 더 어렵다.

해당 장에서는 제네릭 타입으로 만드는 방법을 보여준다.





**Object를 이용한 Stack 클래스**

```java
public class ObjectStack {

    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public ObjectStack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object o) {
        if (elements.length >= DEFAULT_INITIAL_CAPACITY) {
            throw new ArrayIndexOutOfBoundsException("stack is full");
        }

        elements[size++] = o;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        Object result = elements[--size];
        elements[size] = null;
        return result;
    }
}
```



**Generic을 이용한 Stack 클래스 (#1)**

```java
public class GenericStack<E> {

    private E[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    public GenericStack() {
        elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY]; // 컴파일 경고가 뜨기에, 어노테이션을 붙여주었다.
    }

    public void push(E o) {
        if (elements.length >= DEFAULT_INITIAL_CAPACITY) {
            throw new ArrayIndexOutOfBoundsException("stack is full");
        }

        elements[size++] = o;
    }

    public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        E result = elements[--size];
        elements[size] = null;
        return result;
    }
}

```

- item28에서 설명한 것처럼 E와 같은 실체화 불가 타입으로는 배열을 만들 수 없다.
- 이를 해결하기 위해, Object 배열을 생성한 다음 제네릭 배열로 형변환 한다. (작성하기 따라 `type safety`가 깨질 수 있다.)
- 컴파일러는 증명할 수 없지만 개발자가 확인 후에 어노테이션을 붙여주도록 하자.

>  #1과 같은 방식은 형변환을 배열 생성시 단 한번만 해주면 되며 이후에는 그냥 사용하면 된다. 그러나 `힙 오염`이 작성하는 코드에 따라 발생할 수 있다.



**힙 오염**

매개변수화 타입의 변수가 다른 타입이 객체를 참조하면, 힙 오염이 발생한다.

즉 컴파일 시점에서는 컴파일러가 잡아낼 수 없으나 런타임 시점에서 발생할 수 있는 아래와 같은 케이스를 힙 오염이라고 하는 것 같다.  [참고자료](https://parkadd.tistory.com/130)

```java
    private void doSomthing(List<String>... stringList) {
        List<Integer> intList = Arrays.asList(100);
        Object[] objects = stringList;
        objects[0] = intList; // 힙 오염 발생
        String s = stringList[0].get(0); // ClassCastException
    }
```





**Generic을 이용한 Stack 클래스 (#2)**

```java
public class GenericStackV2<E> {

    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;


    public GenericStackV2() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E o) {
        if (elements.length >= DEFAULT_INITIAL_CAPACITY) {
            throw new ArrayIndexOutOfBoundsException("stack is full");
        }

        elements[size++] = o;
    }

    public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        @SuppressWarnings("unchecked")
        E result = (E) elements[--size];
        elements[size] = null;
        return result;
    }
}
```

- 배열을 Object로 두고, pop시에 변환해준다.
- 그러나 기능(메소드)가 추가되면 전부 E로 형변환을 신경 써야 할것 같다.
- 대신 힙오염이 발생하지 않는다.

> 현업에서는 첫번째 방식을 더 선호한다고 한다.



Java 라이브러리내 Stack의 상위 클래스인 Vector은 두번째 방식을 사용한다. (JDK 11.0.11 기준)



**item 28에서 이야기 했던 "배열보다는 리스트를 우선하라" 라는 내용과는 다른 이유**

- 자바가 List를 기본 타입으로 제공하지 않으므로, ArrayList와 같은 제네릭 타입도 결국은 기본 타입인 배열을 사용해 구현해야 한다.
- HashMap 같은 제네릭 타입은 성능을 높일 목적으로 배열을 사용하기도 한다.



제너릭 타입은 기본 타입은 사용할 수 없다. -> 박싱된 기본 타입으로 대체가 가능하다.

제네릭 타입의 하위 타입만 허용하도록 제약을 걸 수도 있다.

```java
class Stack<E extends Number>
```



**핵심 정리**

- 클라이언트에서 직접 형변환해야 하는 타입보다 제네릭 타입이 더 안전하고 쓰기 편하다.
- 새로운 타입을 설계할때는 형변환 없이도 사용할 수 있도록 하라.
- 기존 타입 중 제네릭이었어야 하는게 있다면 제네릭 타입으로 변경하자

