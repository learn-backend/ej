# 옵셔널 반환은 신중히 하라

Java 8 이전에는 메서드가 특정 조건에서 값을 반환할 수 없을 때 아래와 같이 처리할 수 있었다.

- null 반환 (참조타입인 경우)

  > null 반환은 별도의 null 처리 코드를 추가해야하며, 처리를 놓치는 경우 NullPointerException이 실제 원인과는 상관없는 곳에서 발생할 수 있다. 

- 예외 발생

  > 예외 발생은 유연하게 코드를 사용할 수 없다는 단점이 있다



### Optional의 장점

Java 8 부터는 Optional을 지원하며 아래와 같은 장점이 있다.

- 예외를 던지는 메소드보다 유연하고 사용하기 쉽다.
- null을 반환하는 메서드보다 오류 가능성이 적다. (`Optional` 처리를 강제하기에)



### Optional 사용시 주의사항

- `Optional`을 반환하는 메서드에서는 절때 null을 반환하지 말자.

  > null처리와 Optional이 빈 경우 두가지를 예외 처리해주어야 한다.

- `Optional` 객체를 Wrapping 해서 반환하는 것이므로 성능 저하가 생긴다.

- 컬렉션, 스트림, 배열, 옵셔널 같은 컨테이너 타입은 `Optional`로 감싸면 안된다.

  > 컬렉션은 Optional<List<T>> 보다는 빈 List<T>을 반환하는게 좋다.

- int, long, double의 경우 `OptionalInt`, `OptionalLong`, `OptionalDouble`을 사용하자.

  > 덜 중요한 기본타입인 Boolean, Byte, Character, Short, Float은 예외일 수 있다.

- Map의 Key 값으로 절대 사용하면 안된다.

  > Map에 키가 없다는 것을 판단하려면 키 자체가 null 인경우와 Optional.empty()인 경우로 판단해야 한다.

외에 Optional을 바르게 사용하는 방법을 설명하는 방법은 아래 블로그를 참고해보자.

[Optional 제대로 활용하기](https://www.latera.kr/blog/2019-07-02-effective-optional/)



### Optional 활용 방법

- 기본값을 정해둘 수 있다.

  ```java
  String lastWordInLexicon = max(words).orElse("단어 없음.."); 
  String lastWordInLexicon = max(words).orElseGet(() -> getDefaultWord());
  ```

  >  orElse(): Optional에 값이 있을때도 실행되므로 항상 실행된다.
  >
  > orElseGet(): Optional.empty() 인경우에만 실행된다. (초기 설정 비용을 낮출 수 있다.)

- 원하는 예외를 발생시킬 수 있다.

  ```java
  Toy myToy = max(toys).orElseThroew(TemperTantrumException::new);
  ```

- 항상 값이 채워져 있다고 가정한다.

  ```java
  Element lastNobleGas = max(Elements.NOBLE_GASES).get();
  ```

> get()을 호출하였는데, Optional이 비어있으면 `NoSuchElementException` 이 발생하는데, 개인적으로는 orElseThrow로 명확하게 에러를 지정해주는게 명확해보인다.



적합한 메서드를 찾지 못했다면 isPresent 메서드를 통해 원하는 작업을 수행할 수도 있지만, 신중히 사용하여야 한다. 대부분은 위에 언급한 메서드들로 대체할 수 있을 것이다.



Stream을 사용하며, 옵셔널들을 Stream<Optional<T>>로 받아서 채워진 옵셔널들에 값을 뽑아 Stream<T>로 처리하는 코드는 아래와 같다.

```java
streamOfOptionals
	.filter(Optional::isPresent)
	.map(Optional::get)
```



Java 9에서부터는 Optional에 stream() 메서드가 추가되어서 아래와 같이 사용할 수도 있다.

> Optional에 값이 있으면 스트림으로, 없으면 빈 스트림을 반환해준다.

```java
streamOfOptionals
	.flatMap(Optional::stream)
```

