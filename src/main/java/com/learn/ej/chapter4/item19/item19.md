#상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라

- 상속용 클래스는 재정의할 수 있는 메서드들을 내부적으로 어떻게 이용하는지 문서로 남겨야 한다
- 공개된 메서드가 자신의 다른 메서드를 호출하는데 그 메서드가 재정의가 가능한 경우
    - 예시) 18장의 addAll메서드
- 클래스의 내부 동작 과정 중간에 끼어들 수 있는 훅을 잘 선별하여 protected 메서드 형태로 공개
    - 예시) AbstractList의 removeRange 메서드
    - clear 메소드 실행할 때 효율성을 높이기 위해 존재 즉, 외부에서 사용하는 메서드가 아님

###@implSpec 태그
- 자기사용패턴
- 좋은 api 문서란 '어떻게'가 아닌 '무엇'을 하는지를 설명해야 한다라는 격언과 대치됨
- 활성화하려면 -tag "implSpec:a:Implementation Requirements"

###상속을 허용하는 클래스가 지켜야 할 제약
- 상송굥 클래스의 생성자는 직접적으로든 간접적으로든 재정의 가능 메서드를 호출해서는 안 된다
- 상위 클래스의 생성자가 하위 클래스의 생성자보다 먼저 실행되므로 
  하위 클래스에서 재정의한 메서드가 하위 클래스의 생성자보다 먼저 호출된다

생성자 호출 시 Sub의 overrideMe가 호출되고 instant가 정의 전 이므로 null 출력
```java
import java.time.Instant;

public class Super {
    public Super() {
        overrideMe();
    }

    public void overrideMe() {
    }
}

public class Sub extends Super {
    // 프로그램 내에서 instant는 final 멤버이지만 상태가 두 가지가 존재
    private final Instant instant;
    
    public Sub() {
        Instant = Instant.now();
    }
    
    @Override
    public void overrideMe() {
        System.out.println(instant);
    }
}
```
###Cloneable, Serializable를 구현한 상속용 클래스
- clone(), readObject()는 생성자와 비슷한 효과
- 두 메서드 모두 직간접적으로 재정의 가능 메서드를 호출하면 안된다 (위의 예시와 같은 현상 발생)
- Serializable를 구현한 경우 상속을 허용하면 readResolve, writeReplace를 protected로 선언해야 하는데 외부에 공개하는 꼴이 됨 

###일반 클래스의 상속 고려
- 상속용으로 설계하지 않은 클래스는 상속을 금지한다
- 상속을 꼭 허용해야 한다면 (표준 인터페이스를 구현하지 않은 경우) 재정의 가능 메서드를 클래스 내부에서 사용하지 않게 하고 문서화해야 함



