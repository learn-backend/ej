# 익명 클래스보다는 람다를 사용하라



이전 자바(8 이전)에서는 함수 타입을 표현할때 추상 메서드를 하나만 담은 인터페이스를 사용하였다.

```java
    Collections.sort(words, new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            return Integer.compare(s1.length(), s2.length()); // 문자열 길이 기준 정렬
        }
    });
```



자바 8 부터는 함수형 인터페이스를 지원하므로 람다식(lambda expression)을 사용해 만들 수 있다.

- 람다는 함수나 익명 클래스와 개념은 비슷하지만 코드가 간결하다는 장점이 있다.
- 컴파일러가 타입 추론이 가능하기에 s1, s2의 타입 명시를 안해도 된다.
- **타입을 명시해야 코드가 더 명확할 때만 제외**하고는 람다의 모든 매개변수 타입은 생략하자.

```java
Collections.sort(words, (s1, s2) -> Integer.compare(s1.length(), s2.length()));
```



또한 비교자 생성 메서드를 이용해 더 간결하게 가능하다.

```java
Collections.sort(words, Comparator.comparingInt(String::length));
```



더 나아가 List 인터페이스에 추가된 sort 메소드를 이용하면 더욱 짧아진다.

```java
 words.sort(Comparator.comparingInt(String::length));
```

 



또한 아이템 34에서 다뤘던 Operation 의 열거타입도 Lambda를 이용하면 아래와 같이 변경 된다.

**AS-IS**

```java
public enum Operation {
    PLUS{
        public double apply(double x, double y) {
            return x + y;
        }
    }, 
    MINUS{
        public double apply(double x, double y) {
            return x - y;
        }
    }, 
    TIMES{
        public double apply(double x, double y) {
            return x * y;
        }
    }, 
    DIVIDE{
        public double apply(double x, double y) {
            return x / y;
        }
    };
    
    public abstract double apply(double x, double y);    
}
```



**TO-BE (w. DoubleBinaryOperator)**

```java
public enum Operation {
    PLUS((x, y) -> x + y),
    MINUS((x, y) -> x - y),
    TIMES((x, y) -> x * y),
    DIVIDE((x, y) -> x / y);

    private final DoubleBinaryOperator op;

    Operation(DoubleBinaryOperator op) {
        this.op = op;
    }

    public double apply(double x, double y) {
        return op.applyAsDouble(x, y);
    }
}
```

> DoubleBinaryOperator 은 BiFunction<Double, Double, Double> 와 동일하다.



**다만 아래와 같은 경우에는 람다 사용을 하지 않는게 나을수 도 있다.**

- 코드 자체로 동작이 명확히 설명되지 않는 경우
- 코드 줄수가 많아지는 경우 (책에서는 람다는 1~3줄 이내인 게 좋다고 한다.)
- 추상 클래스의 인스턴스를 만들어야 할때 (람다를 사용할 수 없다.)
- 자기 자신을 참조해야 할때 (람다에서의 this 키워드는 바깥 인스턴스이다.)
- 직렬화 해야할때 (람다를 직렬화 하는 일은 구현체마다 다를 수 있으므로 하지 않는 것을 권한다.)





> 그 외에는 람다를 사용하였을때 작은 함수 객체를 아주 쉽게 표현할 수 있다.

