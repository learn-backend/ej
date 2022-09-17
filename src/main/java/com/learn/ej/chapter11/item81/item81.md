# wait와 notify 보다는 동시성 유틸리티를 애용하라

wait과 notify는 올바르게 사용하기가 아주 까다로우니 고수준 동시성 유틸리티를 사용하자.



## java.util.concurrent의 고수준 유틸리티

- 실행자 프레임워크 (Executor Framework)
- 동시성 컬렉션 (concurrent collection)
- 동기화 장치 (synchronizer)



### 동시성 컬렉션 (concurrent collection)

List, Queue, Map같은 표준 컬렉션 인터페이스에 동시성을 구현한 고성능 컬렉션이다.

동시성을 무력화하는 것은 불가능하며 **외부에서 락을 추가로 사용하면 오히려 속도가 느려진다.**

동기화한 컬렉션보다는 동시성 컬렉션을 사용하는 것이 훨씬 좋다. (`Collections.synchronizedMap` 보다는 `ConcurrentHashMap`을 사용하자)



### 컬렉션 인터페이스중 일부는 작업이 성공적으로 완료될때까지 기다리도록 확장되었다.

- BlockingQueue의 take 메서드는 큐의 첫 원소를 꺼내며 비어있다면 새로운 원소가 추가될 때까지 기다린다. (ThreadPoolExecutor을 포함한 대부분의 Executor Seervice는 구현체에서 BlockingQueue를 사용한다.)

  

### 동기화 장치

- 동기화 장치는 스레드가 다른 스레드를 기다릴 수 있게 하여, 서로 작업을 조율할 수 있게 해준다.
- 자주쓰이는 동기화 장치로 `CountDownLatch`, `Semaphore`이 있다. 조금 덜쓰이는 `CyclicBarrier`와 `Exchanger`도 있으며 갖아 강력한 `Phaser`도 있다.



#### CountDownLatch

- 생성자로 int 값을 받으며, 이 값은 countDown 메서드를 몇번 호출해야 대기 중인 스레드를 깨우는지를 결정한다.

어떤동작들을 동시에 시작해 모두 완료하기까지의 시간을 재는 프레임워크를 CountDownLatch를 이용하면 아래와 같이 구현할 수 있다.

> 아래서 사용한 CountDownLatch는 CyclicBarrier(혹은 Phaser) 인스턴스 하나로 대체할 수 있지만, 이해하기는 더 어려워질 수 있다.

```java
public static long time(Executor executor, int concurreny, Runnable action) throws InterruptedException {
	CountDownLatch ready = new CountDownLatch(concurrency);
	CountDownLatch start = new CountDownLatch(1);
	CountDownLatch done = new CountDownLatch(concurrency);
	
	for (int i = 0; i < concurrency ; i++) {
		executor.execute(() -> {
			// 타이머에게 준비를 마쳤음을 알린다.
			ready.countDown(); 
			try {
				// 모든 작업자 스레드가 준비될떄까지 기다린다.
				start.await(); // (2) block -> startNanos가 입력된 다음
				action.run(); // 작업 실행
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				// 타이머에게 작업을 마쳤음을 알린다.
				done.countDown();
			}
		});
	}
	
	ready.await(); // 모든 작업자 스레드가 준비될떄까지 기다린다. // (1) block -> execute 내  ready.countDown() 이 호출될때까지 대기
	long startNanos = System.nanoTime();
	start.countDown(); // 작업자들을 깨운다.
	done.await(); // 모든 작업자가 일을 끝마치기를 기다린다. // (3) block -> finally 블록에 진입후 countDown을 호출할때까지 대기.
	
	return System.nanoTime() - startNanos;
}
```

- time 메서드에 넘겨진 executor은 concurrency 매개변수로 지정한 동시성 수준만큼의 스레드를 생성할 수 있어야 한다. 그렇지 못하면 이 메서드는 결코 끝나지 않으며 **스레드 기아 교착 상태(thread starvation deadlock)에 빠지게 된다.**

- 시간간격을 잴 때는 항상 System.currentTimeMillis가 아닌 System.nanoTime을 사용하자.
  - System.nanoTime은 더 정확하고 정밀하며, 시스템의 실시간 시계의 시간 보정에 영향받지 않는다.
  - 정밀한 시간 측정은 jmh 같은 특수 프레임워크를 사용하라.

```java
public static void main(String[] args) throws InterruptedException {
	long currentTimeStart = System.currentTimeMillis();
	long nanoTimeStart = System.nanoTime();

	Thread.sleep(1000);

	System.out.println("currentTime = " + (System.currentTimeMillis() - currentTimeStart));
	System.out.println("nanoTime = " + (System.nanoTime() - nanoTimeStart));
}
```

<output>

```java
currentTime = 1001
nanoTime = 1023126792
```



## wait()을 사용할때는 반복문 밖에서는 절대로 호출하지 말아라.

```
synchronized (obj) {
	while (<조건 충족되지 않았다>) {
		obj.wait(); // 락을 놓고, 깨어나면 다시 잡는다.
	}
	
	... // 조건이 충족됐을 때의 동작을 수행한다.
}
```

- 대기 전에 조건을 검사하여 조건이 이미 충족되었다면 wait를 건너뛰게 한 것은 응답불가를 예방하는 조치이다.
- 조건이 충족되었는데 스레드가 notify(혹은 notifyAll) 메서드를 호출한 후 대기 상태로 빠지면 그 **스레드를 다시 깨울 수 있다고 보장할 수 없다.**



### 조건이 만족되지 않아도 스레드가 깨어날 수 있는 몇가지 상황이 아래와 같다.

- 스레드가 notify를 호출한 다음 다음 대기중이던 스레드가 깨어나는 사이에 다른 스레드가 락을 얻어 그 락이 보호하는 상태를 변경한다.
- 조건이 만족되지 않았음에도 다른 스레드가 실수로 혹은 악의적으로 notify를 호출한다. 공개된 객체를 락으로 사용해 대기하는 클래스는 이런 위험에 노출된다.
- 깨우는 스레드는 지나치게 관대해서, 대기중인 스레드 중 일부만 조건이 충족되어도 notifyAll을 호출해서 모든 스레드를 깨울 수도 있다.
- 대기 중인 스레드가 (드물게) notify 없이도 깨어나는 경우가 있다. 이를 허위 각성(spurious wakeup) 현상이라 한다.



## notify보다는 notifyAll을 일반적으로 사용하라.

- notify는 스레드 하나만 깨우며, notifyAll은 모든 스레드를 깨운다.
- notifyAll은 모든  스레드가 깨어남을 보장하지만, 깨어난 스레드들은 기다리던 조건이 충족되었는지  확인하기에 프로그램에 정확성에는 영향을 주지 않을것이다.
- 단 하나의 스레드만 혜택을 받을 수 있다면 notify를 통해 최적화 할수 있지만, 그런 스레드가 중요한 notify를 삼켜버린다면 꼭 깨었어야 할 스레드들이 영원히 대기하게 될 수 있다.



> wait과 notify를 직접 사용하는 것은 어셈블리 언어로 프로그래밍 하는것에 비유할 수 있다.
>
> 가능하면 java.util.concurrent와 같이 동시성 유틸리티를 사용하는 것을 권장하며, 혹시 사용하게 된다면 wait은 항상 표준 관용구에 따라 while문 안에서 호출하도록 하자. 또한 notify보다는 notifyAll을 사용하라