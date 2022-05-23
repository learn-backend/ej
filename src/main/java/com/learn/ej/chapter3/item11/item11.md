### 아이템 11. equals를 재정의하려거든 hashCode도 재정의하라
- equals를 재정의한 클래스 모두에서 hashCode도 재정의해야 한다.
- 그렇지 않으면 hashCode 일반 규약을 어기게 되어 해당 클래스의 인스턴스를 HashMap이나 HashSet 같은 컬렉션의 원소로 사용할 때 문제 발생함.

```java
ublic final class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = rangeCheck(areaCode, 999, "area code");
        this.prefix   = rangeCheck(prefix,   999, "prefix");
        this.lineNum  = rangeCheck(lineNum, 9999, "line num");
    }

    private static short rangeCheck(int val, int max, String arg) {
        if (val < 0 || val > max)
            throw new IllegalArgumentException(arg + ": " + val);
        return (short) val;
    }

    @Override public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PhoneNumber))
            return false;
        PhoneNumber pn = (PhoneNumber)o;
        return pn.lineNum == lineNum && pn.prefix == prefix
                && pn.areaCode == areaCode;
    }


    // hashCode 없이는 제대로 동작하지 않는다. 
    // 다음 셋 중 하나를 활성화하자.

    // 전형적인 hashCode 메서드
    // 핵심 필드 3개만 사용하여 간단한 계산만 수행
    @Override public int hashCode() {
        int result = Short.hashCode(areaCode);
        result = 31 * result + Short.hashCode(prefix);
        result = 31 * result + Short.hashCode(lineNum);
        return result;
    }

    // 한 줄짜리 hashCode 메서드 - 성능이 살짝 아쉽다.
    // 입력중 기본타입이 있다면 오토박싱 이슈로 속도가 느리다.
    @Override public int hashCode() {
        return Objects.hash(lineNum, prefix, areaCode);
    }

    // 해시코드를 지연 초기화하는 hashCode 메서드 - 스레드 안정성까지 고려해야 한다.
    // 해시의 키로 사용되지 않는 경우라면 hasCode가 처음 불릴 때 계산하는 지연 초기화를 한다.
    // 필드를 지연 초기화할려면 해당 클래스를 스레드 안전하게 만들도록 고려야해야한다.
    private int hashCode; // 자동으로 0으로 초기화된다.

    @Override public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Short.hashCode(areaCode);
            result = 31 * result + Short.hashCode(prefix);
            result = 31 * result + Short.hashCode(lineNum);
            hashCode = result;
        }
        return result;
    }

    public static void main(String[] args) {
        Map<PhoneNumber, String> m = new HashMap<>();
        m.put(new PhoneNumber(707, 867, 5309), "제니");
        System.out.println(m.get(new PhoneNumber(707, 867, 5309)));
    }
}
```
#### 주의사항
- 성능을 높이기위해 해시코드를 계산할 때 핵심 필드를 생략해서는 안 된다.
- 속도는 빨라지겠지만, 해시 품질이 나빠져 해시테이블의 성능을 심각하게 떨어뜨릴 수도 있다.
- hashCode가 반환하는 값의 생성 규칙을 API 사용자에게 자세히 공표하지 말아야 한다. 
- 그래야 클라이언트가 이 값에 의지하지 않게 되고 추후에 계산 방식을 바꾸룻 있다.
- AutoValue 프레임워크를 사용하면 자동으로 만들어준다.