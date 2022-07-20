# 스트림 병렬화는 주의해서 적용하라
- 동시성 프로그래밍을 할 때는 안정성(safety)과 응답 가능(liveness) 상태를 유지하기 위해 애써야 함

### 무조건 병렬화를 하면 안됨 (메르센 소수)
```java
static Stream<BigInteger> primes() {
    return Stream.iterate(TWO, BigInteger::nextProbablePrime);
}

public static void main(String[]args){
    primes().map(p -> TWO.pow(p.intValueExact()).subtract(ONE))
        .filter(mersenne -> mersenne.isProbablePrime(50))
        .limit(20)
        .forEach(System.out::println);
}
```
- 속도를 높이고 싶어 parallel()을 호출할 경우 아무것도 출력되지 않으면서 cpu는 90퍼센트 점유한(응답 불가) 상태가 무한히 계속됨
- 스트림 라이브러리가 이 파이프라인을 병렬화 하는 방법을 못찾기 때문
- 데이터 소스가 Stream.iterate이거나 중간 연산으로 limit을 쓰면 파이프라인 병렬화로는 성능 개선이 안됨
- 위의 코드는 새로운 소수를 찾을 때마다 그 전 소수를 찾을 때보다 두 배정도 오래 걸림
    - limit 연산은 코어가 남으면 원소를 더 처리하고 이후의 결과를 버려도 된다고 가정한다
    - 코어가 4개이면 20번째 소수를 계산할 때 나머지 코어는 21, 22, 23번째 소수를 계산하고 결과를 얻은 후에 자르게 된다
    - 이 계산은 20번째 소수의 2배, 4배, 8배 정도의 시간이 더 걸리게 된다

### 병렬화의 효과가 좋은 경우
- 스트림 소스가 ArrayList, HashMap, HashSet, ConcurrentHashMap의 인스턴스 이거나 배열, int 나 long 범위
- 데이터를 원하는 크기로 정확하고 손쉽게 나눌 수 있어 다수 스레드에 분배하기 좋다
- 나누는 작업은 Spliterator가 담당, Spliterator 객체는 Stream 이나 Iterable의 spliterator메서드로 얻음

### 병렬수행 효율이 좋은 경우
#### 참조 지역성
- 위의 자료구조들의 공통점
- 이웃한 원소의 참조들이 메모리에 연속해서 저장되어 있음
- 그러나 참조들이 가리키는 실제 객체가 메모리에서 서로 떨어져 있으면 참조 지역성이 나빠짐
- 기본 타입 배열의 경우 참조가 아닌 데이터 자체가 메모리에 연속해서 저장되므로 참조 지역성이 가장 좋음

#### 종단 연산의 동작 방식
- 순차적인 연산이라면 병렬 수행 효과는 제한 됨
- 파이프라인에서 만들어진 원소를 하나로 합치는 축소(reduction) 연산이 병렬화에 가장 적합
- reduce메서드 중 하나 혹은 min, max, count, sum
- collect메서드(가변 축소)는 컬렉션을 합치는 부담이 크기 때문에 적합하지 않음

### 잘 못 병렬화 한 경우
- 스트림을 잘 못 병렬화하면 성능이 나빠질 뿐만 아니라 결과 자체가 잘못되거나 예상 못한 동작이 발생할 수 있음
- 결과가 잘못되거나 오동작 하는 것을 안전실패(safety failure)라고 함
- mappers, filter, 함수객체가 명세대로 동작하지 않을 경우 발생할 수 있음
- Stream 명세는 함수 객체에 규약을 정의해 놓음

#### Stream의 reduce 연산에 건네지는 accumulator(누적기)와 combiner(결합기) 함수 객체 규약
- 결합법칙을 만족 
    - (a op b) op c == a op (b op c)
- 간섭 받지 않음
    - 파이프라인이 수행되는 동안 데이터 소스가 변경되지 않아야 함
- 상태를 갖지 않아야 함

### 스트림 병렬화는 오직 성능 최적화 수단이다
- 반드시 병렬 전후로 성능을 테스트하여 가치를 확인해야 함
- 보통 병렬 스트림 파이프라인도 공통의 포크-조인 풀에서 수행되므로, 잘못된 파이프라인 하나가 시스템의 다른 부분의 성능에 악영할을 줄 수 있음
- 효과적인 예시
```java
static long pi(long n) {
    return LongStream.rangeClosed(2, n)
         .mapToObj(BigInteger::valueOf)
         .filter(i -> i.isProbablePrime(50))
         .count();
}

static long pi(long n) {
    return LongStream.rangeClosed(2, n)
        .parallel()
        .mapToObj(BigInteger::valueOf)
        .filter(i -> i.isProbablePrime(50))
        .count();
}
```

### 무작위 수로 이루어진 스트림의 병렬화
- ThreadLocalRamdom, Random 보다는 SplittableRandom인스턴스를 이용


