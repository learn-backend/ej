### 공유 중인 가변 데이터는 동기화해 사용하라

### 동기화
 - 멀티 스레드 환경에서 메서드나 블록을 한번에 한 스레드씩 수행하도록 보장하는것을 의미

### 동기화 기능
 - 배타적 실행이 가능하다.
   - 객체를 현제 사용중인 스레드만 접근 가능, 객체를 일관된 상태에서 수정할수 있음
 - 스레드 사이에 안정적인 통신
   - 변경된 데이터의 최종 결과값을 읽을수 있게 할수 있음.
   - 동기화 없이는 스레드가 만든 변화를 다른 스레드에서 확인하지 못할수 있음.

### 동기화 자바 언어 
 - 자바 언어 명세상 long, double 이외의 변수를 읽고 쓰는 동작은 원자적(atomic)이다.
   - 원자적: 여러 스레드가 같은 변수를 동기화 없이 수정중이라도, 정상적으로 저장한 값을 온전히 읽어옴을 보장하는 것
 - 원자적이라도 동기화 없이 사용하라는 얘기는 아니다.
   - 스레드가 필드를 읽을때 항상 수정이 반영된 값을 얻는다고 보장하지 않는다.
   - 멀티 스레드 환경에서는 각 스레드 별로 할당 된 CPU Cache로 데이터를 불러와 사용한다.
   - 각각의 CPU Cache에 저장된 데이터가 다를 수 있기에 값 불일치 문제가 발생할 수 있음.


### 동기화기 필요한 상황
```java
public class StopThread {
    private static boolean stopRequested;
    //메인 스레드가 1초 후 stopRequested를 true로 설정하면 
    // 백그라운드 스레드는 반복문을 빠져나올 것처럼 보인다.
    public static void main(String[] args) throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while (!stopRequested)
                i++;
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        stopRequested= true;
    }
}
``` 
 - 동기화를 사용하지 않기 때문에 메인 스레드가 수정한 값을 백그라운드 스레드가 언제 확인할 지 보증할 수 없다.
 - 위에 이유로 JVM이 다음과 같이 최적화를 수행할 수도 있기 때문에 무한루프에 빠진다.
```java
// 원래코드
while (!stopRequested) 
    i++;

// JVM 최적화 코드
if (!stopRequeseted)
    while (true)
        i++;
```

### 동기화 적용한 예제
```java
public class StopThread {

    private static boolean stopRequested;
    
    // 쓰기 synchronized 키워드 사용
    private static synchronized void requestStop() {
        stopRequested = true;
    }
   // 읽기 synchronized 키워드 사용
    private static synchronized boolean stopRequested() {
        return stopRequested;
    }

    public static void main(String[] args) throws InterruptedException {

        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while(!stopRequested()) {
                i++;
            }
        });

        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        stopRequested();
    }
}

```
 - 쓰기 메서드(requestStop)와 읽기 메서드(stopRequested) 모두를 동기화해야한다.
 - 반복문에서 매번 동기화하는 비용이 크진 않지만 volatile 키워드를 이용하면 성능을 개선할 수 있다.

### volatile 키워드
 - 자바 변수를 메인 메모리에 저장하겠다라고 명시해주는 것이다. 
 - CPU Cache가 아닌 메인 메모리의 데이터를 직접적으로 사용(읽기/쓰기)한다. 
 - 따라서 동기화는 아니지만 항상 가장 최근에 기록된 값을 읽게 됨을 보장한다.
```java
public class StopThread {
    
    // volatile 키워드 사용
    // long, double을 제외한 기본타입은 volitile 키워드를 사용하면 동기화를 생략
    private static volatile boolean stopRequested;

    public static void main(String[] args) throws InterruptedException {

        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while(!stopRequested) {
                i++;
            }
        });

        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        stopRequested = true;
    }
}
```

### volatile 주의사항
```java
private static volatile int nextSerialNumber = 0;

public static int generateSerialNumber() {
    return nextSerialNumber++;
}
```
 - 정상적으로 실행될 것 같지만 잘못된 결과를 계산하게 되는 안전 실패(safety failture)가 발생한다.
 - 증감연산자는 실행 시 다음과 같이 nextSerialNumber에 두번 접근하게 되므로, 원자적으로 실행되지 않는다.

```java
private static int nextSerialNumber = 0;
// synchronized 키워드를 붙였으면 volatile는 제거해야한다.
public static synchronized int generateSerialNumber() {
    return nextSerialNumber++;
}
```


### long, double 동기화를 위한 라이브러리
 - Java.util.concurrent.atomic 패키지는 AtomicLong, AtomicDouble을 제공한다.
 - 락 없이도(lock-free) 스레드 안전한 프로그래밍을 지원한다.
 - 성능또한 syncronized보다 뛰어나다.

### 정리
 - 가변 데이터는 공유하지 말고 단일 스레드에서만 쓰도록 하자.
 - 불편 데이터만을 공유하도록 하자.
 - 가변 데이터를 공유해야 한다면 반드시 동기화를 사용하자.
 - 배타적 실행은 필요없고 스레드 간의 통신만 필요한 상황이라면 volatile를 사용하여 처리가 가능하다.