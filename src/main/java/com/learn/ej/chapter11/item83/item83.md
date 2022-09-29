# 지연 초기화는 신중히 사용하라

### 지연초기화
- 필드의 초기화 시점을 그 값이 처음 필요할 때까지 늦추는 기법
- 주로 최적화 용도로 쓰임
- 클래스나 인스턴스 초기화 때 발생하는 위험한 순환 문제를 해결하는 효과도 있음

### 위험성
- 초기화 비용은 줄지만 필드에 접근하는 비용은 커짐
- 초기화 비율, 비용, 빈도에 따라서 성능이 오히려 느려질 수 있음
- 멀티스레드 환경에서는 지연 초기화 하려는 필드을 둘 이상의 스레드가 공유한다면 반드시 동기화 필요
- 필요할 때까지는 하지 않는 것을 추천

### 사용을 해야 하는 경우
- 해당 클래스의 인스턴스 중 그 필드를 사용하는 인스턴스의 비율이 낮고 그 필드를 초기화하는 비용이 큰 경우
- 대부분 상황에서 일반적 초기화가 지연 초기화보다 낫다

### 일반적 초기화
```java
private final FieldType field = computerFieldValue();
```

### 지연 초기화 (synchronized 접근자 방식)
- 지연 초기화가 초기화 순환성을 깨뜨릴 것 같으면 사용
- 초기화 순환성
  - 예를 들어 A class 인스턴스 생성시 B class 인스턴스를 생성하고 B class는 C class를 C class는 A class 인스턴스를 생성하는 경우
```java
private FieldType field;

private synchronized FieldType getField() {
    if (field == null) 
        field = computerFieldValue();
    return field;
}
```

### 지연 초기화 홀더 클래스 관용구
- 클래스는 클래스가 처음쓰일 때 비로소 초기화 된다는 특성을 이용한 관용구
- 성능 때문에 정적 필드를 지연 초기화 해야 하는 경우
```java
private static class FieldHoler {
    static final FieldType field = computeFieldValue();
}

private static FieldType getField() { return FieldHoler.field; }
```
- getField가 호출 되는 순간 FieldHolder 클래스 초기화가 진행됨
- getField 메서드가 필드에 접근하면서 동기화를 전혀 하지 않으니 성능이 느려질 거리가 전혀 없다는 장점
- 일반 VM은 클래스를 초기화 할 때만 필드 접근을 동기화하고 그 이후에는 동기화 코드를 제거하게 됨

### 이중검사 관용구
- 성능 때문에 인스턴스 필드를 지연 초기화 하는 경우
- 초기화 된 필드에 접근할 때의 동기화 비용을 없앰
```java
private volatile FieldType field;

private FieldType getField() {
    FieldType result = field;
    if (result != null) { // 첫 번째 검사 (락 사용 안 함)
        return result;
    }
    
    synchronized(this) {
        if (field == null) // 두 번째 검사 (락 사용)
            field = computeFieldValue();
        return field;
    }
}
```
- 한 번은 동기화 없이 검사하고 두 번째로 필드가 초기화 되지 않았을 때만 필드를 초기화 한다
- 필드가 초기화 된 후로는 동기화하지 않으므로 반드시 필드를 volatile로 선언해야 한다
- result 변수는 필드가 이미 초기화된 상황에서는 그 필드를 딱 한 번만 읽도록 보장
    - 반드시 필요하지 않지만 성능을 높여주고 저수준 동시성 프로그래밍에 표준적으로 사용하는 방법

### 단일검사 관용구
- 이중검사의 변종으로 반복해서 초기화해도 상관없는 인스턴스 필드를 지연 초기화해야 하는 경우 두 번째 검사를 생략
```java
private volatile FieldType field;

private FieldType getField() {
    FieldType result = field;
    if (result == null)
        field = result = computeFieldValue();
    return result;
}
```

### 짜릿한 단임검사 (racy single-check) 관용구
- 모든 스레드가 필드의 값을 다시 계산해도 상관 없고 필드의 타입이 long, double을 제외한 다른 기본 타입인 경우
- volatile 한정자만 없애면 됨
- 필드 접근 속도를 높여주지만, 초기화가 스레드당 최대 한 번 더 이뤄질 수 있음
- 보통 사용 안함

### 정리
- 대부분의 필드는 지연시키지 말고 곧바로 초기화해야 함