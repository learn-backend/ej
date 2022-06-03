# 배열보다는 리스트를 사용하라



**배열과 제네릭 타입에는 중요한 차이가 있다.**

1. 배열은 공변 타입이다. 그러나 제네릭은 불공변 타입이다.

> 공변타입이란?
>
> Sub가 Super의 하위 타입이라면 배열 Sub[]는 배열 Super[]의 하위 타입이 된다.
> 즉 함께 변한다는 뜻이다.

<img width="739" alt="CleanShot 2022-06-03 at 14 36 39@2x" src="https://user-images.githubusercontent.com/37217320/171793144-d55873d4-0f27-4262-85a6-2106a1d11abb.png">



2. **배열은 실체화(reify) 된다.** 즉 런타임에도 자신이 담기로 한 원소의 타입을 인지하고 있다. 그러나, **제네릭은 런타임시 타입이 소거(erasure)** 된다.



이러한 차이로 인해, 배열은 제네릭 타입, 매개변수화 타입, 타입 매개변수로 사용할 수 없다.

Q. 아래 코드는 배열과 제네릭 타입을 함께 쓴 것이 아닐까? (정상 동작)

```java
public class GenericExample<T> {

    private T[] array;

}
```





**실체 불가 타입 (non-reifiable type)**

- 실체화 되지 않아서 컴파일에는 컴파일타임보다 타입 정보를 적게 가지는 타입
- E, List<E>, List<String> ..



배열보다는 리스트를 사용해야 하는 이유를 아래 코드를 통해 확인해보자



```java
public class Chooser {

    private final Object[] arr;

    public Chooser(Collection arr) {
        this.arr = arr.toArray();
    }

    public Object choose() {
        Random rnd = ThreadLocalRandom.current();
        return arr[rnd.nextInt(arr.length)];
    }
}
```



- 이 클래스를 사용하려면 choose 메서드를 호출할 때마다 반환된 Object를 타입 캐스팅 해줘야한다.



```java
public class Chooser<T> {

    private final T[] arr;

    public Chooser(Collection<T> arr) {
        this.arr = (T[]) arr.toArray();
    }

    public T choose() {
        Random rnd = ThreadLocalRandom.current();
        return arr[rnd.nextInt(arr.length)];
    }
}

```

- 제네릭을 사용하여 위와같이 개선 하였다.
- 단 형변환이 런타임에도 안전할 수 없다는 경고 메시지가 뜨긴 한다.
- 물론 컴파일은 가능하며 `@SuppressWarnings("unchecked")` 을 붙여주면 에러를 숨길 수는 있다.



<img width="771" alt="CleanShot 2022-06-03 at 14 53 59@2x" src="https://user-images.githubusercontent.com/37217320/171795191-688fd0e0-a83e-426b-bc13-5ca71e716001.png">



```java
public class Chooser<T> {

    private final List<T> arr;


    public Chooser(Collection<T> arr) {
        this.arr = new ArrayList<>(arr);
    }

    public T choose() {
        Random rnd = ThreadLocalRandom.current();
        return arr.get(rnd.nextInt(arr.size()));
    }
}

```

- 리스트를 사용하면 별도의 오류나 경고 없이 컴파일 된다.
- 코드양이 조금 늘었고, 조금 더 느릴 수는 있지만 런타임시 `ClassCastException`이 만날일은 없다.

