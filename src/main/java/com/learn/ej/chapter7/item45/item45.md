# 스트림은 주의해서 사용하라



스트림 API는 자바 8에 추가되었으며 추상 개념중 핵심 개념은 아래와 같다.

- 유한 혹은 무한 시퀀스 (sequence)
- 스트림 파이프라인



스트림 API는 메서드 연쇄를 지원하는 플루언트 API 이다. (메서드 체이닝을 의미)

- 기본적으로 순차적으로 진행된다.
- parallel 메서드를 호출하면, 병렬로 처리되나 효과를 볼 수 있는 상황은 많지 않다.
  - Reference: [반복문 vs stream vs parallelStream 중 뭐가 제일 빠를까?](https://duck67.tistory.com/30)



### Stream 사용 주의사항

- 스트림을 과용하면 프로그램이 읽거나 유지보수하기 어려워질 수 있다.
  - (의견) 3줄 이상이 된다면, 메서드 분리를 고려해보자.
- 람다에서는 타입이름을 자주 생략하므로 매개변수 이름을 잘지어야 가독성이 올라간다.
- char 값들을 처리할 때는 스트림을 삼가는 편이 낫다.



### Stream 제한 사항

- 람다에서는 final 이거나 effective final 인 경우에만 사용할 수 있다.
- return, break, continue문으로 반복문을 종료할 수 없다.

​	외 다른 주의사항은 아래와 같은 글을 참고해보자.

​		Reference: [[Java8] 자바8 Stream API 주의사항](https://leeyongjin.tistory.com/entry/Java8-%EC%9E%90%EB%B0%948-Stream-API-%EC%A3%BC%EC%9D%98%EC%82%AC%ED%95%AD)



즉 기존 for-loop을 무작정 Stream으로 변경하기보다는 **기존 코드는 스트림을 사용하도록 리팩터링 하되, 새 코드가 더 나아보일때만 반영하자.**



### Stream 사용을 고려하는 사항

아래 일 중 하나를 수행하는 로직이라면 스트림 적용을 검토해보자.

- 원소들의 시퀀스를 일관되게 변환한다.
- 원소들의 시퀀스를 필터링한다.
- 원소들의 시퀀스를 하나의 연산을 사용해 결합한다. (더하기, 연결하기, 최솟값 구하기 등)
- 원소들의 시퀀스를 컬렉션에 모은다. (공통된 속성을 기준으로 묶을때)
- 원소들의 시퀀스에서 특정 조건을 만족하는 원소를 찾는다.



### flatMap

스트림의 원소 각각을 하나의 스트림으로 매핑한 다음 그 스트림들을 다시 하나의 스트림으로 합친다.

```java
 private static List<Card> newDeckUsingForLoop() { // for-loop 이용
        List<Card> result = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                result.add(new Card(suit, rank));
            }
        }
        return result;
    }


    private static List<Card> newDeckUsingStream() { // stream의 flatMap 이용
        return Stream.of(Suit.values())
            .flatMap(suit ->
                Stream.of(Rank.values())
                    .map(rank -> new Card(suit, rank)))
            .collect(Collectors.toUnmodifiableList());
    }
```

