# Lombok @EqualsAndHashCode @ToString 분석



Effective Java 10-12장에 나온 내용을 보고, Lombok에서 지원하는 `@EqualsAndHashCode` 와 `@ToString` 은 어떻게 코드를 생성해주는지 확인해보자.

`equals` 및 `hashCode` 코드를 보면 Effective Java에서 강조한 내용을 담았지만 조금 다른 내용이 있긴 한것 같다.

- hashCode 생성시 59라는 소수를 사용했다.
- null의 경우 0이 아닌, 43을 사용했다.

그러나 상수값이나 특정 값 외 코드 흐름은 전반적으로 좋은 HashCode를 작성하는 방법과 유사해 보인다.

개인적으로, 계속 반복되는 작업이고 직접 정의하면 변경에 대응하기 귀찮아지기에.. Lombok을 사용해도 괜찮지 않을까 싶다.. 

.java File

```java
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class PhoneNumber {

    private final int prefix;
    private final Integer boxingPrefix;

}

```



.class File

```java
public class PhoneNumber {
    private final int prefix;
    private final Integer boxingPrefix;

    // lombok에 의한 생성
    public String toString() {
        return "PhoneNumber(prefix=" + this.prefix + ", boxingPrefix=" + this.boxingPrefix + ")";
    }

   // lombok에 의한 생성
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PhoneNumber)) {
            return false;
        } else {
            PhoneNumber other = (PhoneNumber)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.prefix != other.prefix) {
                return false;
            } else {
                Object this$boxingPrefix = this.boxingPrefix;
                Object other$boxingPrefix = other.boxingPrefix;
                if (this$boxingPrefix == null) {
                    if (other$boxingPrefix != null) {
                        return false;
                    }
                } else if (!this$boxingPrefix.equals(other$boxingPrefix)) {
                    return false;
                }

                return true;
            }
        }
    }

    // lombok에 의한 생성
    protected boolean canEqual(final Object other) {
        return other instanceof PhoneNumber;
    }
   
    // lombok에 의한 생성
    public int hashCode() {
        int PRIME = true;
        int result = 1;
        int result = result * 59 + this.prefix;
        Object $boxingPrefix = this.boxingPrefix;
        result = result * 59 + ($boxingPrefix == null ? 43 : $boxingPrefix.hashCode());
        return result;
    }

    public PhoneNumber(final int prefix, final Integer boxingPrefix) {
        this.prefix = prefix;
        this.boxingPrefix = boxingPrefix;
    }
}
```

