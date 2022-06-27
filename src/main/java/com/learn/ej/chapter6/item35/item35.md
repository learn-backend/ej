# ordinal 메서드 대신 인스턴스 필드를 사용하라

### ordinal 메서드
- 해당 상수가 열거 타입에서 몇 번째 위치인지를 반환하는 메서드
- api 문서 설명 : 이 메서드는 EnumSet과 EnumMap 같이 열거 타입 기반의 범용 자료구조에 쓸 목적으로 설계되었다

### 잘못 사용한 예시
```java
public enum Ensemble {
    SOLO, DUET, TRIO, QUARTET, QUINTET,
    SEXTET, SEPTET, OCTET, NONET, DECTET;

    public int numberOfMusicians() { return ordinal() + 1; }
}
```
- 새로운 상수값 추가 시 문제 발생가능성 있음
    - 8명인 복4중주(double quartet) 추가 불가능
    - 12명인 3중4중주(triple quartet) 추가 시 11번째에 dummy 값 필요
- 순서를 바꿀 시 문제 발생

### 옳은 예시
```java
public enum Ensemble {
    SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5),
    SEXTET(6), SEPET(7), OCTET(8), DOUBLE_QUARTET(8),
    NONET(9), DECTET(10), TRIPLE_QUARTET(12);
    
    private final int numberOfMusicians;
    Ensemble(int size) {
        this.numberOfMusicians = size;
    }
    public int numberOfMusicians() { return this.numberOfMusicians; }
}
```
- 인스턴스 필드를 사용하여 문제점 해결