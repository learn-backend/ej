# 반환 타입으로는 스트림보다 컬렉션이 낫다
- 자바 8 이전의 반환 타입
    - Collection, Set, List와 같은 컬렉션 인터페이스
    - Iterable : for-each 문에서만 쓰이거나 일부 Collection 메서드를 구현할 수 없을 경우
    - 배열 : 성능에 민감하거나 원소들이 기본 타입인 경우
- 자바 8 stream이 도입 된 후의 고민
    - stream은 반복을 지원하지 않으므로 stream과 반복을 잘 조합해야 좋은 코드가 나옴
    - stream으로 반환하면 Iterable을 확장하지 않아서 for-each로 반복이 안되므로 for-each로 반복을 하는 사용는 불편

### stream만 반환할 경우 for-each을 사용하는 법
참고) 스트림을 반환하는 메서드
```java
static Stream<ProcessHandle> allProcesses() {
    return ProcessHandleImpl.children(0);
}
```
- 자바 타입 추론의 한계로 컴파일 안됨
```java
for(ProcessHandle ph : ProcessHandle.allProcesses()::iterator) {
}
```
- 메서드참조를 형변환
  - 스트림을 반복하기 위한 끔찍한 방법
  - ClassCastException 발생 (책에서는 작동한다고 나와 있음)
```java
for(ProcessHandle ph : (Iterable<ProcessHandle>) ProcessHandle.allProcesses().iterator()) {
}
```
- 어댑터를 이용한 방법
```java
for (ProcessHandle ph : iterableOf(ProcessHandle.allProcesses()) {
}

// 어댑터
public static <E> Iterable<E> iterableOf(Stream<E> stream) {
	return stream::iterator;
}
```

### Iterator만 반환할 경우 stream을 사용하는 법
- 어댑터를 이용한 방법
```java
public static<E> Stream<E> streamOf(Iterable<E> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false);
}
```
### 원소 시퀀스를 반환하는 좋은 API
- 공개 API를 작성할 때에는 스트림 파이프라인, 반복문 둘 다 가능하게 해야 한다
- 클라이언트에서 어댑터를 이용하게 하면 코드를 어수선하게 만들고 성능도 2.3배정도 느리다
- Collection 인터페이스는 Iterable 하위타입, stream메서드를 제공
- 원소 시퀀스를 반환할 때에는 Collection이나 그 하위 타입으로 반환 타입을 쓰는 것이 최선이다

### 전용컬렉션을 구현하여 반환
- 반환하는 시퀀스 크기가 작다면 ArrayList나 HashSet 같은 표준 컬렉션 구현체를 반환하는게 최선
- 단지 컬렉션을 반환한다는 이유로 덩치 큰 시퀀스를 메모리에 올리면 안된다
- 반환할 시퀀스가 크지만 표현이 간결하게 되면 전용 컬렉션은 구현하는 방안이 있다

```java
public class PowerSet {
    public static final <E> Collection<Set<E>> of(Set<E> s) {
       List<E> src = new ArrayList<>(s);
       if(src.size() > 30) {
           throw new IllegalArgumentException("집합에 원소가 너무 많습니다(최대 30개).: " + s);
       }

       return new AbstractList<Set<E>>() {
           @Override
           public int size() {
               // 멱집합의 크기는 2를 원래 집합의 원소 수만큼 거듭제곱한 것과 같다
               return 1 << src.size();
           }

           @Override
           public boolean contains(Object o) {
               return o instanceof Set && src.containsAll((Set) o);
           }

           @Override
           public Set<E> get(int index) {
               Set<E> result = new HashSet<>();
               for (int i = 0; index != 0; i++, index >>=1) {
                   if((index & 1) == 1) {
                       result.add(src.get(i));
                   }
               }
               return result;
           }
       };
    }
}
```
- 집합 원소수가 30이 넘으면 PowerSet.of가 예외를 던진다
- Collection.size() 메서드가 int값을 반환하므로 PowerSet.of가 반환되는 시퀀스의 최대 길이는 Integer.MAX_VALUE혹은 2^31-1으로 제한됨
- Stream이나 Iterable은 size에 대한 고민이 없으므로 Collection을 반환할때의 단점이 된다

### Collection 보다 stream이나 Iterable을 반환하는게 나은 경우
- 위의 경우 처럼 AbstractCollection을 활용해서 Collection 구현체를 작성할 때는 Iterable용 메서드 외에 contains, size 2개만 더 구현하면 됨
- contains와 size를 구현하는게 불가능할 때는 stream이나 Iterable을 반환하는 것이 낫다
- 별도의 메서드를 두어 두 방식 모두 제공 할 수도 있다

### 단순히 구현하기 쉬운 쪽을 선택하는 경우
- 부분리스트를 표준 컬렉션에 담으면 입력 리스트 크기의 거듭제곱만큼 메모리를 차지함
- 입력 리스트의 모든 부분리스트를 스트림으로 구현
```java
public class SubList {

    public static <E> Stream<List<E>> of(List<E> list) {
        return Stream.concat(Stream.of(Collections.emptyList()), 
                             prefixes(list).flatMap(SubList::suffixes));
    }

    public static <E> Stream<List<E>> prefixes(List<E> list) {
        return IntStream.rangeClosed(1, list.size())
                        .mapToObj(end -> list.subList(0, end));
    }

    public static <E> Stream<List<E>> suffixes(List<E> list) {
        return IntStream.rangeClosed(0, list.size())
                        .mapToObj(start -> list.subList(start, list.size()));
    }
}
```
- 어떤 리스트의 부분리스트는 그 리스트의 프리픽스의 서픽스에 빈 리스트 하나만 추가하면 된다
- Stream.concat 메서드는 반환되는 스트림에 빈 리스트를 추가하며 flatMap은 모든 프리픅스의 모든 서픽스로 구성된 하나의 스트림을 만듦
- 아래는 위와 같은 내용의 코드
  - 빈 리스트는 반환하지 않음
```java
// for 반복문을 이용한 코드
for (int start = 0; start < src.size(); start++) {
    for (int end = start + 1; end <= src.size(); end++) {
        System.out.println(src.subList(start, end));
    }
}

// for 반복문을 스트림으로 변환
public static <E> Stream<List<E>> of(List<E> list) {
        return IntStream.range(0, list.size())
            .mapToObj(start ->
                IntStream.rangeClosed(start + 1, list.size())
                    .mapToObj(end -> list.subList(start, end)))
            .flatMap(x -> x);
        }
```
