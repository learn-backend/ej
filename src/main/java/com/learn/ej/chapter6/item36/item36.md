# 비트 필드 대신 EnumSet을 사용하라
- 열거 값들을 집합으로 사용하는 경우

### 과거 사용 방식
```java
public class Text {
    public static final int STYLE_BOLD =          1 << 0; //1
    public static final int STYLE_ITALIC =        1 << 1; //2
    public static final int STYLE_UNDERLINE =     1 << 2; //4
    public static final int STYLE_STRIKETHROUGH = 1 << 3; //8
    
    //매개 변수 styles는 0개 이상의 STYLE_ 상수를 비트별 OR 한 값이다.
    public void applyStyle(int styles) {...}
}

// 1 OR 2 => 3
text.applyStyles(STYLE_BOLD | STYLE_ITALIC);
```
- OR을 사용해 여러 상수를 집합으로 모을 수 있음
- 이 집합을 비트 필드라고 함

### 문제점 (열거 상수 문제점 +알파)
- 컴파일 되면 비트필드가 새겨지고 해석이 열거 상수보다 더 어렵다 (어떤 값들이 OR되었는지 역추적 필요)
- 원소 순회가 까다롭다
- API 작성시 최대 몇 비트가 필요한지 예측해야 함

### EnumSet
- 열거 타입 상수의 값으로 구성된 집합을 효과적으로 표현
- 타입 안전, 다른 Set 구현체와도 함께 사용가능
- 내부는 비트벡터로 되어 있음
  - 원소가 64개 이하라면 EnumSet 전체를 long 변수 하나로 표현
- removeAll과 retailAll과 같은 대량 작업은 비트를 효율적으로 처리 할 수 있는 산술 연산을 사용
- 불변 EnumSet을 만들 수 없다
    - Guava 라이브러리에서 구현했지만 EnumSet을 사용해 구현했으므로 성능이 좋지 않다

```java
import java.util.EnumSet;

public class Text {
    public enum Style {BOLD, ITALIC, UNDERLINE, STRIKETHROUGH}

    // 어떤 Set을 넘겨도 되나 EnumSet이 가장 좋다.
    public void applyStyles(Set<Style> styles) {...}
}

text.applyStyles(EnumSet.of(Style.BOLD,Style.ITALIC));
```
