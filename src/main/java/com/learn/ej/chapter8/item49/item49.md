# 매개변수가 유효한지 검사하라
- 매개변수 제약은 받으시 문서화
- 메서드 몸체가 시작되기 전에 검사
- 오류는 가능한 발생한 즉시 잡아야 함

### 매개변수 검사를 데대로 하지 않은 경우
- 메서드 수행 중간에 모호한 예외 던지며 실패할 수 있음
- 메서드가 잘 수행되지만 잘못된 결과 반환
- 메서드가 잘 수행되지만 어떤 객체를 이상한 상태로 만들어 미래 시점에 메서드와 관련 없는 오류 발생

### public, protected 메서드는 매개변수 값이 잘못 됐을 때 던지는 예외 문서화
- @throws 자바독 태그를 사용
- 보통 IllegalArgumentException, IndexOutOfBoundsException, NullPointerException
- @Nullable이나 비슷한 애너테이션을 사용하는 것은 같은 목적으로 사용할 수 있는 애너테이션도 여러가지이고 표준이 아님
- java.util.Objects.requireNonNull 는 사용하기 편하고 원하는 메시지도 지정할 수 있음
```java
this.strategy = Object.requireNonNull(strategy, "전략");
```
- checkFromIndexSize, checkFromToIndex, checkIndex 메서드는 메시지 지정도 불가능하고 유연하지 않지만 제약된 상황 내에서 유용함

### public이 아닌 메서드라면 assert을 사용해 유효성 검증을 할 수 있다
```java
private static void sort(long a[], int offset, int length){
    assert a != null;
    assert offset >= 0 && offset <= a.length;
    assert length >= 0 && length <= a.length - offset;
    ... // 계산 수행
}
```
- assert는 일반적인 유효성 검사와 다름
    - 실패하면 AssertionError를 던짐
    - 런타임에 아무런 효과도, 성능 저하도 없음 (-ea 옵션으로 런차임에 활성화 할 수 있음, 기본은 비활성화)
    
### 주의점
- 메서드가 직접 사용하지 않지만 나중에 쓰기 위해 저장하는 매개변수는 더 신경 써서 검사해야 함
- 나중에 발견이 되면 해당 매개변수를 어디서 가져왔는지 추적하기 어려워 디버깅이 힘듦
- 생성자는 나중에 쓰려고 저장하는 매개변수의 유효성을 검사하라는 원칙의 특수한 사례로 클래스 불변식을 어기는 객체가 만들어지지 않게 하는데 필수

### 메서드 몸체 실행 전에 매개변수 유효성 검사를 해야하는 규칙의 예외
- 유효성 검사 비용이 지나치게 높은 경우
- 계산 과정에서 암묵적으로 검사가 수행되는 경우
- 암묵적 유효성검사에 너무 의존했다가는 실패 원자성을 해칠 수 있음
- 계산 중 잘못된 매개변수 값을 사용해 발생한 예외와 API 문서에서 던지기로 한 예외가 다를 경우 예외 변역 관용구를 이용해 API 문서에 기재된 예외로 번역해야 함