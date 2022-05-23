### 아이템 10. equals는 일반 규약을 지켜 재정의하라
- object는 구체 클래스지만 상속해서 사용이 가능하도록 설겨되어 있다.
- equals, hashCode, toString, clone, finalize 모두 재정의를 염두해두고 설계되어 있음
- 꼭 필요하지 않다면 재정의하지 말고 그냥쓰자
- 재정의가 필요하다만 다섯가지 규약을 지켜서 구현해야 한다.

#### 아래와 같은 케이스는 재정의하지 않는게 좋다.
- 각 인서튼서가 본질적으로 고유함
- 인스턴스의 논리적 동치성 (logical equality)을 검사할 일이 없음
- 상위 클래스에서 재정의한 equals가 하위 클래스에도 딱 들어맞음
- 클래스가 private이거나 package-private이고 equals 메서드를 호출할 일이 없음.

#### equals 메서드를 재정의 필수 규약
- 반사성(reflexivity) : null이 아닌 모든 참조 값 x에 대해, x.equals(x)는 true다.
  * 객체는 자기자신과 같아아한다.
  
- 대칭성(symmetry) : null이 아닌 모든 참조 값 x,y에 대해 x.equals(y)가 true면 y.equals(x)도 true다.
  * 서로에 대한 동치여부는 똑같이 답해야한다.
  
- 추이성(transitivity) : null이 아닌 모든 참조 값 x,y,z에 대해, x.equals(y)가 true이고, y.equals(z)도 true면, x.equals(z)도 true다.
  * 첫번째 객체와 두번째 객체가 같으면 첫번째 객체와 세번째 객체가 같아야 한다.
  
- 일관성(consistency) : null이 아닌 모든 참조 값 x,y에 대해 x.equals(y)를 반복해서 호출하면 항상 true를 반환하거나 항상 false를 반환한다.
  * 두 객체가 같다면(어느 하나 혹은 두 객체 모두가 수정되지 않는 한) 앞으로도 영원히 같아야 한다.
  
- null-아님 : null이 아닌 모든 참조 값 x에 대해 x.equals(null)은 false다
    * 아래 코드의 묵시적 null 검사를 보자. equals가 타입을 확인하지 않으면 잘못된 타입이 인수로 주어졌을 때 ClassCastException을 던져서 일반 규약을 위배하게 된다. 그런데 instanceof는 첫번째 피연산자가 null이면 바로 false를 반환하여 명시적으로 검사할 필요가 없다.

```java
// 명시적 null 검사
@Override
    public boolean equals(Object o) {
        if (o == null) return false;

        ...
    }
// 묵시적 null 검사 (이게 낫다)
@Override
    public boolean equals(Object o) {
        if (!(o instanceof MyType)) return false;

				MyType mt = (MyType) o;
        ...
    }

```
### eauals 메서드 구현후 체크
- 대칭적인가?
- 추이성이 있는가?
- 일관성이 있는가?

```java
public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = rangeCheck(areaCode, 999, "지역코드");
        this.prefix   = rangeCheck(prefix,   999, "프리픽스");
        this.lineNum  = rangeCheck(lineNum, 9999, "가입자 번호");
    }

    private static short rangeCheck(int val, int max, String arg) {
        if (val < 0 || val > max)
            throw new IllegalArgumentException(arg + ": " + val);
        return (short) val;
    }

    @Override public boolean equals(Object o) {
        if (o == this) // 자기자신의 참조인지 확인
            return true;
        if (!(o instanceof PhoneNumber)) //instanceof로 올바른 타입인지 확이
            return false;
        PhoneNumber pn = (PhoneNumber)o; // 올바른 타입으로 형변환
        // 핵심 필드가 모두 일치하는지 검사
        return pn.lineNum == lineNum && pn.prefix == prefix
                && pn.areaCode == areaCode;
    }

    // 나머지 코드는 생략 - hashCode 메서드는 꼭 필요하다(아이템 11)!
}
```


### 주의사항
- equals를 재정의할 때는 hashcode도 반드시 재정의 필요
- 너무 복잡하게 해결하려 들지말자(필드의 동치성만 검사해도 규악을 지킬수 있음)
- equals(with hashcode) 재정의후 구글에서 만든 AutoValue 프레임워크를 활용하여 테스트하자.