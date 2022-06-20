# 제네릭과 가변인수를 함께 쓸 때는 신중하라
- 가변인수는 메서드에 넘기는 인수의 개수를 클라이언트가 조절, 구현방식에 헛점이 있음
- 가변인수 메서드를 호출하면, 가변인수를 담기 위한 배열이 자동으로 생성
- 그 결과 varargs 매개변수에 제네릭이나 매개변수화 타입이 포함되면 컴파일 경고가 발생
- 실제화 불가 타입은 런타임에는 컴파일타임보다 타입관련 정보를 적게 담고 있는 것이 원인
```java
static void dangerous(List<String>... stringLists) {
    List<Integer> intList = List.of(1,2,3);
    Object[] objects = stringLists;
    objects[0] = intList;                    // 힙 오염 발생 
    String string = stringLists[0].get(0);   // ClassCastException 발생
}
```

### @SafeVarargs
- 자바 7에서 추가된 제네릭 가변인수 메서드 작성자가 클라이언트에서 발생하는 경고를 숨길 수 있는 에너테이션
- 메서드 작성자가 그 메서드가 타입 안전함을 보장
- 타입이 안전한 경우
    - 메서드가 배열에 아무것도 저장하지 않음
    - 배열의 참조가 밖으로 노출되지 않음
    - 즉, 매개변수 배열이 순수하게 인수들을 전달하는 일만 하는 경우
    

### 제네릭 매개변수 참조를 노출하는 경우
```java
static <T> T[] toArray(T... args) {
    return args;
}

static <T> T[] pickTwo(T a, T b, T c) {
    switch(ThreadLocalRandom.current().nextInt(3)) {
        case 0: return toArray(a, b);
        case 1: return toArray(b, c);
        case 2: return toArray(c, a);
    }
    throw new AssertiionError();
}

public static void main(String[] args) {
    String[] attributes = pickTwo("좋은", "빠른", "저렴한");
}
```

```java
// 실제로는 아래와 같은 형변환이 일어남
// Object[]는 String[]의 하위타입이 아니므로 형변환할 수 없음
String[] attributes = (String[]) pickTwo("좋은", "빠른", "저렴한");
```
- 예외 
    - @SafeVarargs로 선언된 타입 안전성이 보장된 또 다른 varargs 메서드에 넘기는 것은 안전
    - 배열 내용의 일부 함수를 호출만 하는 (varargs를 받지않는) 일반 메서드에 넘기는 것도 안전????
    

### 제네릭 가변인수 매개변수를 안전하게 사용하는 메서드
- 가변인수 배열 참조를 노출하지 않음 
- 제네릭 타입을 사용하였기 때문에 ClassCastException 또한 발생할 일이 없음
- 안전한 가변인수 메서드에는 @SafeVarargs를 달아서 컴파일러 경고를 없애는 것이 좋음
```java
@SafeVarargs
static <T> List<T> flatten(List<? extends T>... lists) {
    List<T> result = new ArrayList<>();
    for(List<? extends T> list : lists) {
        result.addAll(list);
    }
    return result;
}
```

### 제네릭 가변인수 매개변수를 List로 대체
- 더 좋은 해결방법
- 타입안정성 검증할 수 있고 @SafeVarargs를 달지 않아도 되며 실수로 안전하다고 판단할 일이 없음
- 코드가 조금 지저분하고 성능이 조금 떨어짐
```java
static <T> List<T> flatten(List<List<? extends T>> lists) {
    List<T> result = new ArrayList<>();
    for (List<? extends T> list : lists) {
        result.addAll(list);
    }
    return result;
}
```
