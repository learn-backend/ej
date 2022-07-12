# 표준 함수형 인터페이스를 사용하라



자바가 람다를 지원하면서, API를 작성하는 모범사례가 크게 변경 되었다.

예시로 템플릿 메서드 패턴의 매력이 크게 줄어들었다.



아래와 같이, LinkedHashMap의 `removeEldestEntry` 메서드를 호출하여 true가 반환되면 맵의 새로운 키를 추가하는 put 메서드를 이용하면, 가장 오래된 원소를 제거한다.

> 기본 값은 false를 반환한다.

```java
  Map<String, Object> map = new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Entry<String, Object> eldest) {
                return true;
            }
        };

        map.put("key", 11);
        Object value = map.get("key");
        System.out.println("value = " + value); // null 이 나옴
```



즉 아래 코드는 맵에 원소가 100개 이상이 되면 가장 오래된 원소가 하나씩 제거된다.

```java
 Map<String, Object> map = new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Entry<String, Object> eldest) {
                return size() > 100;
            }
        };
```



이부분을 오늘날 다시 구현한다면, 함수 객체를 받는 정적 팩터리나 생성자를 제공했을 것이라고 한다.

> 그러나, size() 메소드는 인스턴스 메서드인데, 생성자에 넘기는 시점에는 전달할 수 없다.
>
> 이부분이 함수형 인터페이스를 제공받으면 아래와 같이 개선할 수 있다.



책에서 설명한 내용을 기준으로 LinkedHashMap을 상속하여 `removeEldestEntry`을 재정의 하였다.

```java
public class UpgradeLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    private final Predicate<Map<K, V>> eldestPredicate;

    public UpgradeLinkedHashMap() {
        this(m -> false);
    }

    public UpgradeLinkedHashMap(Predicate<Map<K, V>> eldestPredicate) {
        this.eldestPredicate = eldestPredicate;
    }

    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest) {
        return eldestPredicate.test(this);
    }
}
```



**사용하는 클라이언트 코드**

```java
Map<String, Object> upgradeMap = new UpgradeLinkedHashMap<>(m -> m.size() > 100);

upgradeMap.put("key", 11);
Object updValue = upgradeMap.get("key");
System.out.println("upgrade value = " + updValue);
```



java.util.function 패키지에서는 다양한 용도의 표준 함수형 인터페이스가 담겨있다.

**필요한 용도가 있다면 제공해주는 표준 함수형 인터페이스를 사용하라.**



## 표준 함수형 인터페이스

### UnaryOperator<T>

- T타입 인수를 받아 T 타입을 반환해준다.
- 메서드 시그니처: apply

```java
UnaryOperator<Integer> unaryOperator = i -> i + 1;

System.out.println(unaryOperator.apply(1)); // 2
```



### BinaryOperator<T>

- T타입의 인수를 2개 받아 T 타입을 반환해준다.
- 메서드 시그니처: apply

```java
BinaryOperator<Integer> binaryOperator = (i, j) -> i + j;
BinaryOperator<Integer> binaryOperator = Integer::sum; // method reference 이용

System.out.println(binaryOperator.apply(1,2)); // 3
```



### Predicate<T>

- T 타입 인수를 받아 boolean을 반환해준다.
- 메서드 시그니처: test

```java
Predicate<Integer> predicate = i -> i < 10;

System.out.println(predicate.test(5)); // true
```



### Function<T, R>

- T타입 인수를 받아 R 타입을 반환해준다.
- 메서드 시그니처: apply

```java
Function<Integer, String> function = money -> money + "원 입니다.";
  
System.out.println(function.apply(1000)); // 1000원 입니다.
```



### Supplier<T> 

- 인수를 받지 않으며, T 타입을 반환해준다

- 메서드 시그니처: get

```java
Supplier<Integer> supplier = () -> 10;

System.out.println(supplier.get()); // 10
```



### Consumer<T>

- T타입 인수를 받아, 리턴값은 없다.

- 메서드 시그니처: accept

```java
Consumer<String> consumer = s -> System.out.println("s = " + s);

consumer.accept("hello world!"); // output: s = hello world
```



기본형 인터페이스는 int, long, double용으로 변형이 생겨난다.

xxxFunction, xxxConsumer ... 

```java
ex) IntFunction, DoubleFunction, LongFunction
```



그외 제공해주는 함수형 인터페이스가 총 43개 존재하지만, 위 패턴을 변형하여 사용되어지며, 필요할 때 찾아 쓸 수 있도록 범용적인 이름을 사용했다.

또한 기본 함수형 인터페이스 (IntFunction, DoubleConsumer... )에 박싱된 기본 타입을 사용하면 Auto-Unboxing으로 성능이 느려질 수 있다.





## 커스텀 함수형 인터페이스를 사용해야 하는 경우

대부분은 제공해주는 함수형 인터페이스를 사용하되, 아래와 같은 경우에는 커스텀 함수형 인터페이스를 고려해라 (예를 들면 Comparator가 있다.)

- 자주 쓰이며, 이름 자체가 용도를 명확히 설명해준다.
- 반드시 따라야 하는 규약이 있다.
- 유용한 디폴트 메서드를 제공할 수 있다.





## @FunctionalIntercae

해당 애너테이션은 @Override의 목적과 유사하며 아래와 같은 장점을 가진다.

- 해당 클래스의 코드나 설명 문서를 읽을 이에게 람다용으로 설계된 내용을 공유 해준다.
- 추상 메서드를 오직 하나만 가지고 있어야 컴파일 되게 해준다.
- 유지보수 과정에서 실수로 메서드를 추가하지 못하게 해준다.

즉 가능하면 직접 만든 함수형 인터페이스에는 항상 `@FunctionalInterface`을 붙여주어라



## 서로 다른 함수형 인터페이스를 다중 정의(Overload)하지 말라

- 클라이언트에게 불필요한 모호함을 안겨주며 클라이언트 형변환이 필요하다.
- 가능하면 다른 함수형 인터페이스를 같은 위치의 인수로 사용하는 다중 정의를 피해라

```java
executorService.submit(() -> System.out.println("submit"));
executorService.submit((Callable<Object>) () -> "1");
```



