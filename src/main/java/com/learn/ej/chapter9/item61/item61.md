# 박싱된 기본 타입보다는 기본 타입을 사용하라
- 오토박싱, 오토언박싱을 통해 기본타입, 박싱타입을 구분하지 않고 사용할 수 있지만 차이가 사라지는 것은 아니다

### 기본 타입과 박싱된 기본 타입의 차이
- 기본 타입은 값만 가지고 있으나, 박싱된 기본 타입은 식별성이란 속성을 갖는다
    - 박싱된 기본 타입은 두 인스턴스 값이 같아도 서로 다르다고 식별될 수 있다
- 기본 타입의 값은 언제나 유효하나 박싱된 기본 타입은 null을 가질 수 있다
- 기본 타입이 박싱된 기본 타입보다 시간과 메모리 사용면에서 더 효율적이다

### 비교자를 사용한 경우
```java
Comparator<Integer> naturalOrder =
        (i,j) -> (i < j) ? -1 : (i == j ? 0 : 1);
```
- naturalOrder.compare(new Integer(42), new Integer(42))의 결과는? 1을 출력
- i == j 부분에서 두 객체 참조의 식별성 검사를 하기 때문
- i,j가 서로다른 인스턴스이므로 i == j 의 결과는 false 이다 
- 박싱된 기본 타입에 == 연산자를 사용하면 안된다

문제를 수정한 비교자
```java
Comparator<Integer> naturealOrder = (iBoxed, jBoxed) -> {
    int i = iBoxed, j = jBoxed; // 오토박싱
    return i < j ? -1 : (i == j ? 0 : 1);
};
```

### 기본 타입과 박싱된 기본 타입을 혼용한 연산에서는 박싱된 기본 타입의 박싱이 자동으로 풀린다
```java
public class Unbelievable {
    static Integer i;
    
    public static void main(String[] args) {
        if (i == 42)
          System.out.println("믿을 수 없군");
    }
}
```
- 위의 결과는 "믿을 수 없군"출력이 아니라 NullPointerException이 발생한다
- null 참조를 언박싱 하기 때문이다

### 성능 이슈
```java
public static void main(String[] args) {
    Long sum = 0L;
    for (long i = 0 ; i <= Integer.MAX_VALUE; i++) {
        sum += i;
    }
    System.out.println(sum);
}
```
- 박싱과 언박싱이 반복해서 일어나서 성능이 체감될 정도로 느려진다

### 박싱된 기본 타입을 사용해야 하는 경우
- 컬렉션의 원소, 키, 값으로 쓰인다
  - 컬렉션은 기본 타입을 담을 수 없다
- 매개변수화 타입이나 매개변수화 메서드의 타입 매개변수로 사용할 때 쓰인다
- 리플렉션을 통해 메서드를 호출할 때도 박싱된 기본 타입을 사용해야 한다