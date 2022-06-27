# ordinal 인덱싱 대신 EnumMap을 사용하라

### 잘못된 예시
- ordinal 값을 배열의 인덱스로 사용하는 경우
```java
class Plant {
    enum Lifecycle { ANNUAL, PERENNIAL, BIENNIAL }
    final String name;
    final Lifecycle lifeCycle;
    
    Plant(String name, Lifecycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    } 
    
    @Override public String toString() {
        return name;
    }
}

Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];
for (int i = 0 ; i < plantsByLifeCycle.length ; i++) {
    plantsByLifeCycle[i] = new HashSet<>();
}

for (Plant p : garden) {
    plantsByLifeCycle[p.lifeCycle.ordinal()].add(p);
}

// 결과 출력
for (int i = 0; i < plantsByLifeCycle.length; i++) {
    System.out.printf("%s: %s%n", Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);
}
```
문제점
- 배열은 제네릭과 호환 되지 않으므로 비검사 형변환 수행 필요 -> 깔끔한 컴파일 안됨
- 배열은 각 인덱스의 의미 모름 -> 출력 결과에 직접 레이블 달아야함
- 정확한 정숫값을 사용한다는 것을 보장해야 함 (ordinal 값은 바뀔 수 있음)

### EnumMap 사용
```java
Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(Plant.LifeCycle.class);
for (Plant.LifeCycle lc : Plant.LifeCycle.values()) {
    plantsByLifeCycle.put(lc, new HashSet<>());
}
for (Plant p : garden) {
    plantsByLifeCycle.get(p.lifeCycle).add(p);
}
```
- 출력 결과에 레이블을 달 필요 없음
- 배열 인덱스 계산하는 과정 오류 가능성 없음
- 타입안정성, 배열의 성능 모두 얻음
    - EnumMap의 성능이 ordinal을 쓴 배열과 같은 이유는 EnumMap 내부에서 ordinal을 사용한 배열을 사용

### Stream 사용
```java
//HashMap을 이용
Arrays.stream(garden)
      .collect(groupingBy(p -> p.lifeCycle))

//EnumMap을 이용
Arrays.stream(garden)
    .collect(groupingBy(p -> p.lifeCycle, () -> new EnumMap<>(LifeCycle.class), toSet()));
```
- HashMap을 이용한 방식
    - 존재하는 열거 타입에 대한 맵의 키만 만듦
- EnumMap을 이용한 방식
    - 모든 열거 타입에 대한 키 만듦

