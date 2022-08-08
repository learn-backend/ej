# 라이브러리를 익히고 사용하라

### 라이브러리를 익히지 않고 사용한 경우 예시
```java
static Random rnd = new Random();

static int random(int n) {
    return Math.abs(rnd.nextInt()) % n;
}
```
- n이 그리 크지 않은 2의 제곱수이면 얼마 지나지 않아 같은 수열이 반복된다
- n이 2의 제곱수가 아니라면 몇몇 숫자가 평균적으로 더 자주 반복된다
- n값이 크면 이 현상이 더 두드러진다

```java
public static void main(String[] args) {
    int n = 2 * (Integer.MAX_VALUE / 3);
    int low = 0;
    for(int i = 0; i < 1000000; i++) {
        if(random(n) < n/2) {
            low++;
        }
        System.out.println(low);
    }
}
```
- random 메서드가 이상적으로 동작한다면 약 50만개가 출력되어야 한다
- 실제로는 666666에 가까운 값을 얻는다
- random 메서드의 세번째 결함으로는 지정한 범위 바깥의 수가 종종 튀어나온다
    - rnd.nextInt()가 반환한 값을 Math.abs를 이용해 음수가 아니 ㄴ정수로 매핑하기 때문
    - nextInt()가 Integer.MIN_VALUE를 반환하면 Math.abs도 Integer.MIN_VALUE를 반환하고
    - 나머지 연산자(%)는 음수를 반환해버린다 (n이 2의 제곱수가 아닌 경우)

해결
- 위의 문제는 Random.nextInt(int)로 인해 해결이 되었다
- 표준라이브러리를 사용하면 그 코드를 작성한 전문가의 지식과 앞서 사용한 다른 프로그래머들의 경험을 활용할 수 있다

### 표준 라이브러리를 쓰는 이유
- 핵심적인 일과 크게 관련 없는 문제를 해결하느라 시간을 허비하지 않아도 된다
- 따로 노력하지 않아도 성능이 지속해서 개선된다
- 기능이 점점 많아진다
- 우리가 작성한 코드가 많은 사람들에게 낯익은 코드가 된다

### 정리
- 많은 프로그래머들이 라이브러리에 기능이 있는지 모르고 직접 구현을 한다
- 적어도 java.lang, java.util, java.io 와 그 하위 패키지에는 익숙해져야 한다
- 특히 컬렉션 프레임워크, 스트림 라이브러리, java.util.concurrent 동시성 기능은 알아두면 큰 도움이 된다
- 자바 표준 라이브러리에서 기능을 찾지 못하면 고품질의 서드파트 라이브러리를 찾고 그래도 없으면 직접 구현하라