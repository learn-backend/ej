# int 상수 대신 열거 타입을 사용하라

### 정수 열거 패턴
```java
public static final int APPLE_FUJI = 0;
public static final int APPLE_PIPPIN = 1;
public static final int APPLE_GRANNY_SMITH = 2;

public static final int ORANGE_NAVEL = 0;
public static final int ORANGE_TEMPLE = 1;
public static final int ORANGE_BLOOD = 2;
```
- 타입 안전을 보장할 방법이 없다
    - APPLE_FUJI와 ORANGE_NAVEL은 동등하다 (APPLE_FUJI==ORANGE_NAVEL 은 true)
- 프로그램이 깨지기 쉽다
    - 컴파일 하면 그 값이 클라이언트 파일에 그대로 새겨진다
    - 상수 값이 바뀌었는데 클라이언트가 다시 컴파일 하지 않으면 엉뚱하게 동작할 수 있다
- 정수 상수는 문자열로 출력하기 까다롭다
    - 값을 출력하거나 디버거로 보면 숫자만 보인다 
- 문자열 열거 패턴은 더 나쁘다
    - 오타를 컴파일러과 확인 못함
    - 성능 떨어짐
    
### 열거 타입
```java
public enum Apple {FUJI, PIPPIN, GRANNY_SMITH}
public enum Orange {NAVEL, TEMPLE, BLOOD}
```
1. final
    - 열거 타입 자체는 클래스
    - 상수 하나당 자신의 인스턴스를 하나씩 만들어 public static final 필드로 공개
    - 생성자를 제공하지 않으므로 사실상 final, 열거 타입 내 필드도 final


2. 타입 안전성
    - APPLE_FUJI와 ORANGE_NAVEL은 비교할 수 없다 (APPLE.FUJI==ORANGE.NAVEL 에러가 발생한다)
    

3. 각자의 이름공간(네임스페이스)이 있다
    - 이름이 같은 상수도 공존한다
    - 상수 값이 바뀌어도 클라이언트가 다시 컴파일 하지 않아도 된다 (클라리언트 파일에 정수 값이 각인되지 않는다)
   
 
4. 데이터와 메서드를 추가할 수 있다
    - 모든 필드는 final이어야 한다 -> 프로젝트 개선 필요
    - 필드를 private로 선언 후 접근자 메서드를 제공하는 것이 좋다
    
### 상수마다 동작이 달라져야 하는 메서드
```java
public enum Operation {
    PLUS, MINUS, TIMES, DIVIDE
}
```

- switch문을 이용
```java
public double apply(double x, double y) {
    switch(this) {
        case PLUS: return x + y;
        case MINUS: return x - y;
        case TIMES: return x * y;
        case DIVIDE: return x / y;
    }
    throw new AssertionError("알 수 없는 연산: " + this);
}
```

- 상수별 메서드 구현 
    - 추상 메서드를 선언하고 각 상수에서 재정의
    - 정의하지 않으면 컴파일 오류 발생
```java
public enum Operation {
    PLUS{
        public double apply(double x, double y) {
            return x + y;
        }
    }, 
    MINUS{
        public double apply(double x, double y) {
            return x - y;
        }
    }, 
    TIMES{
        public double apply(double x, double y) {
            return x * y;
        }
    }, 
    DIVIDE{
        public double apply(double x, double y) {
            return x / y;
        }
    };
    
    public abstract double apply(double x, double y);    
}
```

### fromString 메서드
- toString 메서드를 재정의 하려거든 toString이 반환하는 문자열을 열거 타입으로 변환하는 fromString 메서드도 제공하는 것을 고려해보라
- 단, 타입 이름을 적절히 바꿔야 하고 모든 상수의 문자열 표현이 고유해야 함

### 열거 타입 상수 일부가 같은 동작을 공유 
- 상수별 메서드 구현에는 열거 타입 상수끼리 코드를 공유하기 어렵다


- switch문을 이용하는 경우
    - 관리관점에서 위험하다
        - 새로운 값을 열거 타입에 추가려면 case문도 넣어야 한다
        - 새로운 상수를 추가하면서 overtimePay 를 재정의하지 않으면 평일용 코드를 물려받는다
```java
enum PayrollDay {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
    
    private static final int MINS_PER_SHIFT = 8 * 60;
    
    int pay(int minutesWorked, int payRate) {
        int basePay = minutesWorked * payRate;
        int overtimePay;
        switch(this) {
            case SATURDAY:
            case SUNDAY:
                overtimePay = basePay / 2;
                break;
            default:
                overtimePay = minutesWorked <= MINS_PER_SHIFT ? 
                        0 : (minutesWorked - MINS_PER_SHIFT) * payRate / 2;
        }
        return basePay + overtimePay;
    }
}
```

- 전략 열거 타입 패턴
    - 계산을 private 중첩 열거 타입으로 옮김
```java
enum PayrollDay {
    MONDAY(WEEKDAY),
    TUESDAY(WEEKDAY),
    WEDNESDAY(WEEKDAY),
    THURSDAY(WEEKDAY),
    FRIDAY(WEEKDAY),
    SATURDAY(WEEKEND),
    SUNDAY(WEEKEND);
    
    private final PayType payType;
    
    PayrollDay(PayType payType) {
        this.payType = payType;
    }
    
    int pay(int minutesWorked, int payRate) {
        return payType.pay(minutesWorked, payRate);
    }
    
    // 전략 열거 타입
    enum PayType {
        WEEKDAY {
            int overtimePay(int minutesWorked, int payRate) {
                return minutesWorked <= MINS_PER_SHIFT ? 0 : (minutesWorked - MINS_PER_SHIFT) * payRate / 2;
            }
        },
        
        WEEKEND {
            int overtimePay(int minutesWorked, int payRate) {
                return minutesWorked * payRate / 2;
            }
        };
        
        abstract int overtimePay(int minutesWorked, int payRate);
        private static final int MINS_PER_SHIFT = 8 * 60;
        
        int pay(int minutesWorked, int payRate) {
            int basePay = minutesWorked & payRate;
            return basePay + overtimePay(minutesWorked, payRate);
        }
    }
}
```

- switch문을 사용하면 좋은 경우
    - 기존 열거 타입에 상수별 동작을 혼합해 넣을 경우
```java
// 기존 열거 타입에 없는 기능을 수행
public static Operation inverse(Operation op) {
    switch(op) {
        case PLUS: return Operation.MINUS;
        case MINUS: return Operation.PLUS;
        case TIMES: return Operation.DIVIDE;
        case DIVIDE: return Operation.TIMES;
    }
}   
```

### 정리
- 필요한 원소를 컴파일 타임에 다 알 수 있는 상수 집합인 경우 항상 열거 타입 사용
- 열거 타입에 정의된 상수 개수가 영원히 불변일 필요 없음
