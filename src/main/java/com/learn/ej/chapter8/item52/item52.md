### 다중정의(오버로딩)는 신중히 사용하라

```java
public class CollectionClassifier {
    public static String classify(Set<?> s) {
        return "집합";
    }

    public static String classify(List<?> lst) {
        return "리스트";
    }

    public static String classify(Collection<?> c) {
        return "그 외";
    }

    public static void main(String[] args) {
        Collection<?>[] collections = {
                new HashSet<String>(),
                new ArrayList<BigInteger>(),
                new HashMap<String, String>().values()
        };
        
        // 컴파일 시점에는 항상 Collection<?>
        for (Collection<?> c : collections)
            System.out.println(classify(c));
    }
}
```
- 위에 예제는 순차적으로 출력할거 같지만 "그 외" 호출된다.
- 다중 정의된 메서드 중 어느 메서드를 호출할지는 컴파일 시점에 정해진다. 
- 재정의한 메서드는 동적으로 선택되고, 다중 정의한 메서드는 정적으로 선택된다.

#### 재정의된 메서드 호출 매커니즘
```java
class Wine {
    String name() { return "포도주"; }
}

class SparklingWine extends Wine {
    @Override String name() { return "발포성 포도주"; }
}

class Champagne extends SparklingWine {
    @Override String name() { return "샴페인"; }
}

public class Overriding {
    public static void main(String[] args) {
        List<Wine> wineList = List.of(
                new Wine(), new SparklingWine(), new Champagne());

        for (Wine wine : wineList)
            System.out.println(wine.name());
    }
}
```
- 예상대로 순차적으로 출력한다.
- 가장 하위에서 정의한 재정의 메서드가 실행되기 떄문

### 람다와 메서드 참조 도입시 혼란
```java
new Thread(System.out::println).start(); //컴파일 성공

ExecutorService exec = Executors.newCachedThreadPool();
exec.submit(System.out::println); //컴파일 에러
```
- 인수가 똑같고 양쪽모두 Runnable를 받는 형제 메서드를 다중 정의하고 있다.
- 원인은 submit 다중 정의 메서드 중에는 Callabe<T>를 받논 메서드도 있다는데 있다.
- 다중 정의 해소 알고리즘은 우리의 기대처럼 동작하지 않는다.
- 따라서 메서드를 다중정의할때, 서로 다른 함수형 인터페이스라도 같은 위치의 인수로 받아서는 안된다.
  - -Xlint:overloads를 지정하면 이런 종류의 다중 정의를 경고해줄수 있다.
### 다중 정의가 아닌 모든 메서드에 다른 이름을 지어 분리하는 방법
 - ObjectOutputStream 클래스
 - writeBoolean(boolean), writeInt(int), writeLong(long) 같은 구조
 


#### 정리
 - 매개변수 수가 같을때는 다중정의를 피하는게 좋다.
 - 헷갈릴만한 매개변수는 형변환하여 정확한 다중정의 메서드가 선택되도록 해야한다.
 - 가변인수를 사용하는 메서드라면 다중 정의를 사용하지 말자.
 - 다중 정의가 무조건 정답이 아니다. 메서드 이름을 다르게 지어 분리하는것도 방법이다.