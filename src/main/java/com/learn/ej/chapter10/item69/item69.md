# 예외는 진짜 예외 상황에만 사용하라

예외는 오직 예외 상황에서만 사용해야 하며, 절대로 일상적인 제어 흐름용으로 쓰여선 안된다.

아래는 배열을 순회하는 방식을 잘못 사용한 예이다.

```java
try {
  int i = 0;
  while (true) {
    range[i++].climb();
  }
} catch (ArrayIndexOutOfBoundsException e) {}
```



표준적인 관용구대로 작성했다면 아래와 같다.

```java
for (Mountain m: range) {
	m.climb();
}
```

Effective Java의 저자의 컴퓨터에서 원소 100개 짜리 배열로 테스트해보니, 위 코드가 2배정도 느렸다고 한다.



## 잘 설계된 API라면 클라이언트가 정상적인 제어 흐름에서 예외를 사용할 일이 없게 해야한다.

- **상태 의존적 메서드**를 제공하는 클래스는 **상태 검사 메서드**도 함께 제공해야 한다.



예를들어 Iterator의 hasNext가 존재하는 경우와 존재하지 않는 경우를 비교해보자.

- 반복문에 예외를 사용하면 장황하고 헷갈리며, 속도도 느리고, 엉뚱한 곳에서 발생한 버그를 숨기기도 한다.

hasNext가 존재하는 경우

```java
for (Iterator<Foo> i = collection.iterator(); i.hasNext(); ) {
  Foo foo = i.next();
	...
}
```



hasNext가 없는 경우

```java
try {
	Iterator<Foo> i = collection.iterator();
	while (true) {
		Foo foo = i.next();
		...
	}
} catch (NoSuchElementException e) {}
```





## 상태 검사 메서드 대안

상태 검사 메서드 대신 빈 옵셔널 혹은 null 같은 특수한 값을 반환하는 방법도 있으며, 아래와 같은 상황에 고려해볼 수 있다.

1. 외부 동기화 없이 여러 스레드가 동시에 접근할 수 있거나, 외부 요인으로상태가 변할 수 있는 경우
   - 상태 검사 메서드와 상태 의존적 메서드 호출 사이에 객체의 상태가 변할 수 있기 때문이다.
2. 성능이 중요한 상황에서 상태 검사 메서드가 상태 의존적 메서드의 작업 일부를 중복 수행해야 하는경우

위 케이스 외에는 **상태 검사 메서드가 아래와 같은 근거**로 조금 더 낫다고 할 수 있다.

- 조금 더 나은 가독성
- 잘못 사용했을때 발견하기 쉽다.

