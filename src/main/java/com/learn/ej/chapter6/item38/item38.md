### 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라
 - 열거 타입은 거의 모든 상황에서 타입 안전 열거패턴보다 뛰어남
 - 연산 코드를 제외하고는 대부분의 상황에서 열거타입을 확장하는건 좋지 않다.
 - 열거 타입을 확장할려면 인터페이스를 정의하고 열거 타입이 해당 인터페이스를 구현하면 된다.

```java
public interface Operation {
    double apply(double x, double y);
}

public enum ExtendedOperation implements Operation {
    EXP("^") {
        public double apply(double x, double y) {
            return Math.pow(x, y);
        }
    },
    REMAINDER("%") {
        public double apply(double x, double y) {
            return x % y;
        }
    };
    private final String symbol;
    ExtendedOperation(String symbol) {
        this.symbol = symbol;
    }
    @Override public String toString() {
        return symbol;
    }

    // 열거 타입의 Class 객체를 이용해 확장된 열거 타입의 모든 원소를 사용하는 방법
    public static void main(String[] args) {
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);
        test(ExtendedOperation.class, x, y);
    }
    private static <T extends Enum<T> & Operation> void test(
            Class<T> opEnumType, double x, double y) {
        for (Operation op : opEnumType.getEnumConstants())
            System.out.printf("%f %s %f = %f%n",
                    x, op, y, op.apply(x, y));
    }

    // 컬렉션 인스턴스를 이용해 확장된 열거 타입의 모든 원소를 사용하는 방법
    public static void main(String[] args) {
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);
        test(Arrays.asList(ExtendedOperation.values()), x, y);
    }
    private static void test(Collection<? extends Operation> opSet,
                             double x, double y) {
        for (Operation op : opSet)
            System.out.printf("%f %s %f = %f%n",
                    x, op, y, op.apply(x, y));
    }
}
```
### 자바라이브러리에서 사용하는 예제 
```java
/**
 * Defines the options as to how symbolic links are handled.
 *
 * @since 1.7
 */

public enum LinkOption implements OpenOption, CopyOption {
    NOFOLLOW_LINKS;
}

```
#### 정리
- 인터페이스와 그 인터페이스를 구현하는 기본 열거타입을 함께 사용해 같은 효과를 낼수 있다.
- 해당 방법은 열거 타입끼리 구현을 상속할수 없는 사소한 문제가 있음.