# 스트림에서는 부작용 없는 함수를 사용하라

### 스트림 패러다임
- 계산을 일련의 변환(transformation)으로 재구성
- 각 변환 단계는 가능한 이전 단계의 결과를 받아 처리하는 순수 함수이어야 함
    - 순수함수란, 입력만이 결과에 영항을 주는 함수
- 다른 가변 상태 참조하지 않고 함수 스스로도 다른 상태 변경하지 않아야 함

### 패러다임을 깨는 예시
- forEach에서 와부 상태(freq)를 수정함
```java
Map<String, Long> freq = new HashMap<>();
try(Stream<String> words = new Scanner(file).tokens())
    words.forEach(word -> {
        freq.merge(word.toLowerCase(), 1L, Long::sum);
    });
}
```

### 올바르게 수정
- forEach가 다른 상태를 바꾸거나 계산하는데 쓰이지 않음
- 코드가 읽기 쉬워 유지보수가 쉬워짐
- 참고) forEach 연산은 병렬화가 안되고 스트림과 어울리지 않아 계산이 아닌 계산 결과를 보고할 때만 사용
```java
Map<String, Long> freq;
try (Stream<String> words = new Scanner(file).tokens()) {
    freq = words.collect(groupingBy(String::toLowerCase, counting()));
}
```

### 수집기(collector)
- 스트림의 원소들을 객체 하나에 취합
- toList(), toSet(), toCollection(collectionFactory)를 이용해 각각 리스트, 집합, 지정 컬렉션타입으로 반환

### toMap()
**인수가 2개**
- 가장 간단한 방법
```java
Collector<T, ?, Map<K,U>> toMap(Function<? super T, ? extends K> keyMapper,
  Function<? super T, ? extends U> valueMapper)
```
```java
private static final Map<String, Operation> stringToEnum = 
    Stream.of(values()).collect(
        toMap(Obejct::toString, e->e));
```
**인수가 3개**
- 키가 같은 모든 값을 병합한 결과를 얻는 병합함수 인수 추가
- 병함함수 : BinaryOperator (U는 해당 값의 맵 타입)
```java
Collector<T, ?, Map<K,U>> toMap(Function<? super T, ? extends K> keyMapper,
    Function<? super T, ? extends U> valueMapper,
    BinaryOperator<U> merge Function)
```
```java
Map<Artist, Album> topHits = albums.collect(
    toMap(Album::artist, a->a, maxBy(comparting(Album::sales))));
```
- 충돌이 나면 마지막 값을 취하는 수집기를 만들때도 유용
```java
toMap(keyMapper, valueMapper, (oldVl, newVal) -> newVal)
```
**인수가 4개**
- 맵 팩터리 인수 추가
- EnumMap, TreeMap 같은 원하는 특정 맵 구현체 지정
```java
Collector<T, ?, Map<K,U>> toMap(Function<? super T, ? extends K> keyMapper,
  Function<? super T, ? extends U> valueMapper,
  BinaryOperator<U> mergeFunction,
  Supplier<M> mapSupplier)
```
```java
Map<Artist, Album> topHits = albums.collect(
    toMap(Album::artist, a->a, maxBy(comparting(Album::sales), HashMap::new)));
```

### groupingBy()
- 분류함수를 입력받고 원소들을 카테고리벼로 모아 놓은 맵을 담은 수집기를 출력
- 분류함수는 입력받은 원소가 속하는 카테고리 반환하고 해당 카테고리가 해당 원소의 맵의 키로 쓰임

**인수가 1개**
- 반환된 맵에 담긴 각각의 값은 해당 카테고리에 속하는 원소들을 담은 리스트
```java
Collector<T,?,Map<K,List<T>>> 
  groupingBy(Function<? super T,? extends K> classifier)
```
```java
words.collect(groupingBy(word -> alphabetize(word)))
```

**인수가 2개**
- 해당 카테고리의 모든 원소를 담은 스트림으로부터 값을 생성하는 다운스트림 수집기 명시
```java
Collector<T,?,Map<K,D>>
  groupingBy(Function<? super T,? extends K> classifier, 
    Collector<? super T,A,D> downstream)
```
```java
Map<String, Long> freq = words.collect(groupingBy(String::toLowerCase, counting()));
```

**인수가 3개**
- 반환되는 맵과 그 안에 담긴 컬렉션 타입 모두 지정 가능
- 다운스트림 매개변수 앞에 놓임 (점층적 인수 목록 패턴 위반)
```java
Collector<T,?,M>
  groupingBy(Function<? super T,? extends K> classifier, 
  Supplier<M> mapFactory, Collector<? super T,A,D> downstream)
```
```java
words.collect(groupingBy(word -> alphabetize(word),
        () -> new HashMap<>(String.class),
        toList())))
```

**groupingByConcurrent**
- 위의 세가지 경우에 각각에 대응하는 ConcurrentMap생성

### 그 밖의 수집기
- partitioningBy
- 다운스트림 수집기 전용 (int, long, double 스트림용으로 각각 존재)
  - counting()
  - summing()
  - averaging()
  - 등등...
  
### 수집과 관련이 없는 메서드
- maxBy, minBy
  - 값이 가장 큰 혹은 작은 원소 반환
- joining
  - 문자열에만 사용
  - 인수가 없으면 단순히 연결
  - 인수가 1개면 매개변수로 받은 구분문자를 연결부위에 삽입
  - 인ㅅ가 3개면 접두문자, 구준문자, 접미문자를 매개변수로 받아 문자열 생성