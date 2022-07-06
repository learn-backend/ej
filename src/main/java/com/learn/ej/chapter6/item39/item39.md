### 명명 패턴보다 애너테이션을 사용하라
- 전통적으로 도구나 프레임워크는 명명패턴을 사용해왔다.
- 명명패턴은 몇가지 단점이 있다.
  - 오타발생시 인식 불가능
  - 원하는 프로그램 요소에서만 사용되리라는 보장이 없음 
  - 프로그램 요소를 매개변수로 전달할수 았는 마땅한 방법이 없음

#### 애너테이션은 이 모든 문제를 해결해주는 개념으로 JUnit도 버전4부터 전면 도입하였다.
```java
/**
 * 테스트 메서드임을 선언하는 애너테이션이다.
 * 매개변수 없는 정적 메서드 전용이다.
 */
@Retention(RetentionPolicy.RUNTIME) // @Test가 런타임에도 유지
@Target(ElementType.METHOD) // @Test가 반드시 메서드 선언에서만 사용 
public @interface Test { // @Test
}

/**
 * 명시한 예외를 던져야만 성공하는 테스트 메서드용 애너테이션
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    Class<? extends Throwable> value();
}

```
### 반복 가능한 애너테이션 다루기
```java
/**
 *  반복 가능한 애너테이션 타입 
 *  자바8부터 지원되는 방법
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ExceptionTestContainer.class)
public @interface ExceptionTest {
    Class<? extends Throwable> value();
}

/**
 * 반복 가능한 애너테이션의 컨테이너 애너테이션
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTestContainer {
    ExceptionTest[] value(); // 컨테이너 애너테이션은 내부 애너테이션 타입의 배열을 반환하는 value 메서드를 정의필수
}
public class RunTests {
    public static void main(String[] args) throws Exception {
        int tests = 0;
        int passed = 0;
        Class testClass = Class.forName(args[0]);
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                tests++;
                try {
                    m.invoke(null);
                    passed++;
                } catch (InvocationTargetException wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    System.out.println(m + " 실패: " + exc);
                } catch (Exception exc) {
                    System.out.println("잘못 사용한 @Test: " + m);
                }
            }

            // 코드 39-10 반복 가능 애너테이션 다루기
            if (m.isAnnotationPresent(ExceptionTest.class)
                    || m.isAnnotationPresent(ExceptionTestContainer.class)) {
                tests++;
                try {
                    m.invoke(null);
                    System.out.printf("테스트 %s 실패: 예외를 던지지 않음%n", m);
                } catch (Throwable wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    int oldPassed = passed;
                    ExceptionTest[] excTests =
                            m.getAnnotationsByType(ExceptionTest.class);
                    for (ExceptionTest excTest : excTests) {
                        if (excTest.value().isInstance(exc)) {
                            passed++;
                            break;
                        }
                    }
                    if (passed == oldPassed)
                        System.out.printf("테스트 %s 실패: %s %n", m, exc);
                }
            }
        }
        System.out.printf("성공: %d, 실패: %d%n",
                          passed, tests - passed);
    }
}
```
### @Repeatable 주의점
- Repeatable을 단 애너테이션을 반환하는 '컨테이너 애너테이션'을 하나 더 정의하고, @Repeatable에 컨테이너 애너테이션의 class 객체를 매개변수로 전달해야 한다
- 컨테이너 애너테이션은 내부 애너테이션 타입의 배열을 반환하는 value 메서드를 정의해야 한다.
- 컨테이너 에너테이션 타입에는 적절한 보존(@Retention)과 대상(@Target)을 명시해야 한다.


#### 정리
- 자바 프로그래머라면 예외없이 자바가 제공하는 애너테이션 타입들을 사용해야한다.
- 애네테이션으로 할수 있는 일을 명명 패턴으로 처리할 이유는 없다.