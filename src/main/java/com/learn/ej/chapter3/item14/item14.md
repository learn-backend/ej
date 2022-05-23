# Comparable을 구현할지 고려하라



Comparable 인터페이스는 compareTo 메소드를 단독으로 가지고 있다.

> FunctionalInterface 조건에 충족한다.

```java
public interface Comparable<T> {
	 public int compareTo(T o);
}
```

```java
public class ComparableMain {
    public static void main(String[] args) {
        Comparable comparable = o -> 0;
    }
}
```



**compareTo 특징**

- equals와 같이 동치성 비교를 할 수 있다.
- 순서까지 비교할 수 있으며, 제네릭하다.

> 사실상 Java 플랫폼 라이브러리의 모든 값 클래스와 열거 타입이 Comparable을 구현했다.



**compareTo 일반 규약**

- 이 객체와 주어진 객체의 순서를 비교한다.
  - 작다: 음수
  - 같다: 0
  - 크다: 양수
  - 비교할 수 없는 타입의 객체: ClassCastException()

- sgn(x.compareTo(y)) == -sgn(y.compareTo(x))
- x.compareTo(y) > 0 && y.compareTo(z) 이면, x.compareTo(z) > 0 이다.
- x.compareTo(y) == 0 이면, sgn(x.compareTo(z)) == sgn(y.compareTo(z)) 이다.

> 위 규약은 Object의 equals의 규약을 가지며, 필수는 아니지만 지키는 것이 좋다. 만약 해당 권고를 지키지 않는다면 반드시 그 사실을 명시해주자.





정렬된 컬렉션들은 동치성을 비교할 때 equals 대신 compareTo를 사용한다.

예시로 BigDecimal 클래스는 compareTo와 equals가 일관되지 않는다.

> BigDecimal 클래스는 equals는 String 값 자체를, compareTo시에는 long과 int로 변환후 비교하는 것 같다.

```java
class BigDecimalTest {

    @Test
    void bigDecimalTest() {
        BigDecimal bigDecimal1 = new BigDecimal("1.0");
        BigDecimal bigDecimal2 = new BigDecimal("1.00");

        assertThat(bigDecimal1)
            .isEqualByComparingTo(bigDecimal2) // compareTo 사용했을때는 같음
            .isNotEqualTo(bigDecimal2); // equals를 사용하면 같지 않음
    }
}
```



그렇다면, 정렬 알고리즘을 활용하는 TreeSet과의, 정렬 알고리즘을 사용하지 않는 HashSet의 결과를 비교해보자.

```java
    @Test
    void hashSetTest() {
        BigDecimal bigDecimal1 = new BigDecimal("1.0");
        BigDecimal bigDecimal2 = new BigDecimal("1.00");

        Set<BigDecimal> hashSet = new HashSet<>();
        hashSet.add(bigDecimal1);
        hashSet.add(bigDecimal2);

        assertThat(hashSet).hasSize(2); //  equals를 사용했을때 두개
    }


    @Test
    void treeSetTest() {
        BigDecimal bigDecimal1 = new BigDecimal("1.0");
        BigDecimal bigDecimal2 = new BigDecimal("1.00");

        Set<BigDecimal> hashSet = new TreeSet<>();
        hashSet.add(bigDecimal1);
        hashSet.add(bigDecimal2);

        assertThat(hashSet).hasSize(1); // compareTo를 사용했을때 한개
    }
}
 
```

위 결과를 보면, `hashSet`은 `equals` 메서드로 비교하므로 2개, `TreeSet`은 `compareTo`를 사용하기에 1개를 가지게 된다.



- compareTo 메서드에서 관계 연산자 `>` 또는 `<` 를 사용하는 방식은 오류를 발생할 수 있으니 추천하지 않는 방법이다.
- **Java 7부터 새로 추가된 정적 메서드 compare을 사용하자.**

**권장하는 방식 #1**

```java
public int compareTo(PhoneNumber pn) {
	int result = Short.compare(areaCode, pn.areaCode);
	if (result == 0) {
		result = Short.compare(prefix, pn.prefix);
		if (result == 0) {
			result = Short.compare(lineNum, pn.lineNum);
		}
	}
	return result;
}
```



Java8 부터 Comparator 인터페이스가 메소드 체이닝 방식으로 비교자를 생성할 수 있다.

> 간결한 장점이 있지만 약간의 성능 저하가 뒤따를 수 있다.

**권장하는 방식 #2**

```java
public int compareTo(PhoneNumber pn) {
	return compareingInt((PhoneNumber pn) -> pn.areaCode)
		.thenComparingInt(pn -> pn.prefix)
		.thenComparingInt(pn -> pn.lineNum)
		.compare(this, pn);
}
```



번외: Effective Java 저자의 PC에서는 #2의 방식이 #1의 방식보다 10%정도 성능이 느려졌다고 한다.  이 결과가 생각과는 달라, 테스트를 해보았다.



```java
package com.example.examplesource.ej.item14;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class PhoneNumber implements Comparable<PhoneNumber> {

    private final int areaCode;
    private final int prefix;
    private final int lineNum;

    @Override
    public int compareTo(PhoneNumber pn) {
        int result = Integer.compare(areaCode, pn.areaCode);
        if (result == 0) {
            result = Integer.compare(prefix, pn.prefix);
            if (result == 0) {
                result = Integer.compare(lineNum, pn.lineNum);
            }
        }

        return result;
    }
}

```



```java
package com.example.examplesource.ej.item14;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

class CompareToTest {

    private List<PhoneNumber> phoneNumbers = new ArrayList<>();

    @BeforeEach
    void init() {
        Random rnd = new Random();
        for (int i = 0; i < 1_000_000; i++) {
            phoneNumbers.add(PhoneNumber.builder()
                .areaCode(rnd.nextInt(100))
                .prefix(rnd.nextInt(100))
                .lineNum(rnd.nextInt(9_999_999) + 10_000_000)
                .build());
        }
    }

    @Test
    void compareToSpeedTest() {
        ArrayList<PhoneNumber> dumpPhoneNumbers = new ArrayList<>(phoneNumbers);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("compareTo #1");
        Collections.sort(phoneNumbers);
        stopWatch.stop();

        stopWatch.start("Comparator #2");
        dumpPhoneNumbers.sort(
            Comparator.comparingInt(PhoneNumber::getAreaCode)
                .thenComparingInt(PhoneNumber::getPrefix)
                .thenComparingInt(PhoneNumber::getLineNum));
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());

    }
}

```





결과는 저자의 말처럼, 약 10%정도 성능 차이가 나는것을 볼 수 있다.

> Comparator을 새로 생성하는 것과 관련이 있을까..?

```java
---------------------------------------------
ns         %     Task name
---------------------------------------------
381691083  038%  compareTo #1
631626291  062%  Comparator #2
```









