# 스레드보다는 실행자, 태스크, 스트림을 애용하라

java.util.concurrent 패키지내 실행자 프레임워크(Executor Framework)라고 하는 인터에피스 기반의 유연한 테스크 실행을 담고 있다.

> 즉 스레드를 직접 생성하는 것보다 뛰어난 작업 큐를 다음과 같이 한줄로 생성할 수 있다.

```java
ExecutorService exec = Executors.newSingleThreadExecutor();
```



## 실행자 서비스(Executor Framework)의 주요 기능

- 특정 태스크가 완료되기를 기다린다. (`get()`)
- 태스크 모음 중 아무것 하나 (`invokeAny()`) 혹은 모든 태스크 (`invokeAll()`)가 완료되기를 기다린다.
- 실행자 서비스가 종료하기를 기다린다. (`awaitTermination()`)
- 완료된 태스크들의 결과를 차례로 받는다. (`ExecutorCompletionService` 이용)
- 태스크를 특정 시간에 혹은 주기적으로 실행하게 한다. (`ScheduledThreadPoolExecutor` 이용)



ThreadPoolExecutor 클래스를 이용해 스레드 풀 동작을 결정하는 거의 모든 속성을 사용할 수도 있다.

### Executors.newCachedThreadPool()

- 특별히 설정할게 없고 간단하게 사용할 수 있다.
- 그러나 요청받은 태스크들이 큐에 쌓이지 않고 즉시 스레드에 위임된다. (스레드가 없으면 스레드가 생성된다.)
- 무거운 프로덕션 서비스에서는 스레드 개수를 제한할 수 없으므로 치명적일 수 있다.



### `Executors.newFixedThreadPool(size)` or `new ThreadPoolExecutor`

- 스레드 수를 제한할 수 있어 CPU 이용률이 치닫는 현상을 제한할 수 있다.



실행자 서비스(Executor Framework) 에서는 작업 단위와 실행 메커니즘이 분리되며, 작업 단위를 나타내는 핵심 추상 개념이 **태스크(Task)** 이다.

태스크(Task)는 두가지가 있다.

- Runnable: 반환값이 없고 단순 실행을 정의한다.
- Callable: 값을 반환하고 임의의 예외를 던질 수 있다.



Java7이 되면서 실행자 서비스(Executor Framework) 에서는 포크-조인(fork-join) 태스크를 지원하도록 확장되었다. **포크-조인 태스크는 포크-조인 풀이라는 특별한 실행자 서비스가 실행해준다.**

```java
ExecutorService executorService = new ForkJoinPool();
```

ForkJoinTask의 인스턴스는 작은 하위 태스크로 나뉠수 있고, ForkJoinPool을 구성하는 스레드들이 이 태스크들을 처리하며, 일을 먼저 끝낸 스레드는 **다른 스레드의 남은 태스크**를 가져와 대신 처리할 수도 있다.

모든 스레드가 바쁘게 움직여 CPU를 최대한 활용하면서 높은 처리량곽 낮은 지연시간을 달성한다, 다만 포크-조인에 적합한 형태의 작업이어야 한다. (참고: [Class ForkJoinTask<V>](https://runebook.dev/ko/docs/openjdk/java.base/java/util/concurrent/forkjointask))