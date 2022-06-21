# 한정적 와일드카드를 사용해 API 유연성을 높이라

### 불공변 방식
`List<String>`은 `List<Object>`가 하는 일을 제대로 수행하지 못하므로 하위타입이 될 수 없다 (리스코프 치환 원칙)

불공변 방식보다 더 유연한 방식이 필요할 수 있다

### E 매개변수에 와일드 카드 타입 적용
```java
public class Stack{

    public void pushAll(Iterable<E> src) {
        for (E e : src) {
            push(e);
        }
    }
    
    public void popAll(Collection<E> dst) {
        while(!isEmpty()) {
            dst.add(pop());
        }
    }

    // 한정적 와일드 카드 타입 적용
    public void pushAll(Iterable<? extends E> src){
        for(E e : src){
            push(e);
        }
    }

    // 한정적 와일드 카드 타입 적용
    public void popAll(Collection<? super E> dst) {
        while(!isEmpty()) {
            dst.add(pop());
        }
    }
    
}
```
- 한정적 와일드 카드 타입 적용시 유연성을 가지게 됨
```java
Stack<Number> numberStack = new Stack<>();
Iterable<Integer> integers = List.of(1,2,3,4,5);
numberStack.pushAll(intergers);

Stack<Number> numberStack = new Stack<>();
Collection<Object> objects = List.of("String", 1.0, 0);
numberStack.popAll(objects);
```
- PECS
    - producer-extends, consumer-super
    

- 입력 매개변수가 생산자와 소비자 역할을 동시에 한다면 타입을 정확하게 지정해야 하므로 와일드카드 타입을 쓰면 안된다


- item30의 union 함수에 적용
```java
// 반환 타입에는 한정적 와일드카드 타입을 사용하면 안된다 클라이언트도 와일드카드 타입을 쓰게 되기 때문
public static <E> Set<E> union(Set<? extends E> s1, Set<? extends E> s2) {
    Set<E> result = new HashSet<>();
    result.addAll(s2);
    return result;
}

Set<Integer> integers = Set.of(1,2,3);
Set<Double> doubles = Set.of(4.0, 5.0, 6.0);
Set<Number> numbers = union(integers, doubles);
```


- item30의 max 함수에 적용
    - Comparable<E> -> Comparable<? super E>
    - Collection<E> -> Collection<? extends E>
```java
public static <E extends Comparable<? super E>> Optional<E> max(Collection<? extends E> c) {
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
```
- ScheduledFuture<V> 같은 경우 Comparable을 구현하지 않았기 때문에 위와 같이 max함수에 한정적 와일드카드 타입을 사용해야만 max함수를 사용할 수 있다
```java
public interface Comparable<E>
public interface Delayed extends Comparable<Delayed>
public interface ScheduledFuture<V> extends Delayed, Future<V>
```

- 비한정적 타입 매개변수 vs 비한정적 와일드카드
    - public API라면 두번째 방식이 더 낫다 (더 간단하고 포괄적) //////////////////////////////**??? 왜지???**
    - 어떤 리스트든 이 메서드에 넘기면 명시한 인덱스의 원소들을 교환해줄 것이다. 신경 써야 할 타입 매개변수도 없다
    - 메서드 선언에 타입 매개변수가 한 번만 나오면 와일드카드로 대체
    - 이때 비한정적 타입 매개변수라면 비한정적 와일드카드로 바꾸고, 한정적 타입 매개변수라면 한정적 와일드카드로 바꾸면 된다
```java
public static <E> void swap(List<E> list, int i, int j);
public static void swap(List<?> list, int i, int j);
```

```java
// 리스트의 타입이 List<?>인데, List<?>에는 null외에 어떤 값도 넣을 수 없으므로 도우미 메서드 사용 
// 런타임 오류를 낼 수 있는 형변환이나 리스트 로타입보다 나은방법이다
public static void swap(List<?> list, int i, int j) {
    swapHelper(list, i, j);
}

private static <E> void swapHelper(List<E> list, int i, int j) {
    list.set(i, list.set(j, list.get(i)));
}
```