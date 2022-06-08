# 이왕이면 제네릭 메서드로 만들라



클래스와 마찬가지로 메서드도 제네릭으로 만들 수 있다.

- 타입 매개변수 목록은 메서드의 제한자와 반환 타입 사이에 온다.

```java
public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
	Set<E> result = new HashSet<>();
	result.addAll(s2);
	return result;
}
```



- 타입을 여러개 받고 싶다면 아래와 같이 사용하면 된다. (ex: BiFunction)

  ```java
  @FunctionalInterface
  public interface BiFunction<T, U, R> {  
      R apply(T t, U u);
  
      default <V> BiFunction<T, U, V> andThen(Function<? super R, ? extends V> after) {
          Objects.requireNonNull(after);
          return (T t, U u) -> after.apply(apply(t, u));
      }
  }
  ```



**재귀적 타입 한정**

- 자기 자신이 들어간 표현식을 사용하여 허용 범위를 한정하고 싶을때 사용하면 된다.

예를들어, Comparable 인터페이스를 구현한 타입이 비교할 수있는 원소의 타입만을 정의 하고자 한다면, 아래와 같이 사용할 수 있다.

```java
public class RecursiveTypeExample {

    public static void main(String[] args) {

        Integer maxValue = max(List.of(1, 2, 3))
            .orElseThrow(IllegalArgumentException::new);

        System.out.println("max = " + maxValue); // max = 3
    }


    public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
        if (c.isEmpty()) {
            throw new IllegalArgumentException("Collection is empty!");
        }

        Optional<E> result = Optional.empty();
        for (E e : c) {
            if (result.isEmpty() || e.compareTo(result.get()) > 0) {
                result = Optional.of(e);
            }
        }

        return result;
    }

}

```

E는 Comparable을 상속한 타입만 받을 수 있기때문에, `compareTo` 메소드를 사용할 수 있다.





**핵심 정리**

- 제네릭 타입과 마찬가지로, 제네릭 메서드가 더 안전하며 사용하기도 쉽다.
- 형변환을 해줘야 하는 기존 메소드는 제네릭 메서드로 만들자.
- 코드의 변경 없이 여러 케이스를 수용할 수 있다.