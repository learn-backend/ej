### 가변인수는 신중히 사용하라
 - 가변인수 메서드는 명시한 타입의 인수를 0개 이상 받을수 있다.
 - 인수의 개수와 길이가 같은 배열을 만들고 인수들을 배열에 저장하여 넘겨주는 구조.

```java
public class Varargs {
    //가변인수 활용 
    static int sum(int... args) {
        int sum = 0;
        for (int arg : args)
            sum += arg;
        return sum;
    }

    // 인수가 1개 이상이어야 하는 가변인수 메서드 - 잘못 구현한 예제
    static int min(int... args) {
        if (args.length == 0)
            throw new IllegalArgumentException("인수가 1개 이상 필요합니다.");
        int min = args[0];
        for (int i = 1; i < args.length; i++)
            if (args[i] < min)
                min = args[i];
        return min;
    }

    // 인수가 1개 이상이어야 할 때 가변인수를 제대로 사용하는 방법
    // 첫번째는 평범한 매개변수를 받고, 가변인수는 두번째로 받아서 처리하자.
    static int min(int firstArg, int... remainingArgs) {
        int min = firstArg;
        for (int arg : remainingArgs)
            if (arg < min)
                min = arg;
        return min;
    }
    
}
```
### 성능 이슈 및 대응 패턴
 - 성능 이슈: 가변인수 메서드는 호출할때마다 배열 생성 및 초기화를 한다.
 - 대응 패턴
   - 예를 들어 매개변수 메서드의 호출의 95% 가 인수를 3개 이하로 사용한다고 해보자.
   - 그렇다면 0개부터 4개인 것까지, 총 5개를 정의하자. 
   - 마지막 다중정의 메서드가 인수 4개 이상인 5%의 호출을 담당하는 것이다.
```java
public void foo(){}
public void foo(int a1){}
public void foo(int a1, int a2){}
public void foo(int a1, int a2, int a3){}
public void foo(int a1, int a2, int a3, int... rest){}
```
 - 메서드 호출중 단 5%만이 배열을 생성한다.
#### 정리
 - 인수 개수가 일정하지 않는 메서드를 정의해야한다면 가변인수가 필요하다.
 - 메서드를 정의할때 필수 매개변수는 가변인수 앞에두고, 가변인수를 사용할때는 성능문제도 고려하자.