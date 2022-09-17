# 과도한 동기화는 피하라

과도한 동기화는 **성능을 떨어뜨리고**, **교착상태에도 빠뜨리며**, **심지어 예측할 수 없는 동작**을 낳기도 한다.

동기화 블록 안에서는 제어를 **절대로 클라이언트에게 양도**하면 안된다. 예를 들어 재정의할 수 있는 메서드를 호출하면 안되며, 클라이언트가 넘겨준 함수 객체를 호출해서도 안된다. 동기화영역을 포함한 클래스 관점에서는 이런 메서드는 모두 바깥 세상에서 온 `외계인(alien)` 이라고 이다.

- 클라이언트에게 양도하게되면 **응답 불가** 또는 **안전 실패**가 발생할 수 있다.

#### 응답 불가 (liveness failure)

```
A liveness failure occurs when an activity gets. into a state such that it is permanently unable to make forward progress. One form of liveness. failure that can occur in sequential programs is an inadvertent infinite loop, where the code. that follows the loop never gets executed.
```

#### 안전 실패 (Safety fail)

```
The situation when a safety related system or component fails to perform properly in such a way that it calls for the system to be shut down or the safety instrumented function to activate when there is no hazard present.
```

## 

## 동기화 블록 안에서 외계인 메서드를 호출하는 예시

```java
@FunctionalInterface
public interface SetObserver<E> {

    void added(ObservableSet<E> set, E element);
}
```



```java
public class ObservableSet<E> extends ForwardingSet<E> {

    private final List<SetObserver<E>> observers = new ArrayList<>();

    public ObservableSet(Set<E> s) {
        super(s);
    }

    public void addObserver(SetObserver<E> observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public boolean removeObserver(SetObserver<E> observer) {
        synchronized (observers) {
            return observers.remove(observer);
        }
    }

    @Override
    public boolean add(E element) {
        boolean added = super.add(element);
        if (added) {
            notifyElementAdded(element);
        }

        return added;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean result = false;
        for (E element: c) {
            result |= add(element);
        }
        return result;
    }

    private void notifyElementAdded(E element) {
        synchronized (observers) {
            for (SetObserver<E> observer: observers) {
                observer.added(this, element);
            }
        }
    }
}

```





### 외계인 메서드 사례 #1

**ConcurrentModificationException 발생**

```
set.addObserver(new SetObserver<>() {
	public void added(ObservableSet<Integer> s, Integer e) {
		System.out.println(e);
		if (e == 23)
			s.removeObserver(this);
	}
});
```

0 - 99까지 출력해주는 프로그램인데, 그 값이 23이면 자기 자신을 제거하는 관찰자를 추가해보자.

> 23까지 출력한 후 ConcurrentModificationException을 던진다. `add` 메서드 호출이 일어난 시점에, `notifyElementAdded` 내 `observers` 리스트를 순회하고 있어, 삭제시 에러가 발생한다.





### 외계인 메서드 사례 #2 

**DeadLock 발생**

```java
 set.addObserver(new SetObserver<>() {
            @Override
            public void added(ObservableSet<Integer> s, Integer e) {
                System.out.println(e);
                if (e == 23) {
                    ExecutorService exec = Executors.newSingleThreadExecutor();
                    try {
                        exec.submit(() -> s.removeObserver(this)).get();
                    } catch (ExecutionException | InterruptedException ex) {
                        throw new AssertionError(ex);
                    } finally {
                        exec.shutdown();
                    }
                }
            }
        });

```



구독해지를 하는 관찰자를 작성하는데, `removeObserver`을 실행자서비스(ExecutorService)를 이용해 다른 스레드로 호출해보자.

> s.removeObserver를 호출하면 관찰자를 잠그려 시도하지만 `메인쓰레드에서 락`을 쥐고 있다. 그와 동시에 메인 스레드는 백그라운드 스레드가 관찰자를 제거하기만을 기다리는 중이므로 교착상태에 빠진다.



위와 같은 문제는 **외계인 메서드 호출을 동기화 블록 밖**으로 옮기면 된다.

```java
    // #2 외계인 메서드를 동기화 블록 밖에서 호출하는 경우
private void notifyElementAddedIssueFixed(E element) {
    List<SetObserver<E>> snapshot = null;
    synchronized (observers) {
        snapshot = new ArrayList<>(observers);
    }

    for (SetObserver<E> observer : snapshot) {
        observer.added(this, element);
    }
```

자아의 동시성 컬렉션 라이브러리의 `CopyOnWirteArrayList`가 이러한 목적으로 설계된 클래스이다.

즉 순회할때 락이 필요 없어 매우 빠르다. (수정할일이 드물고 순회만 빈번히 일어나는 관찰자 리스트 용도로는 최적이다.)







## 동기화 기본 규칙

- 동기화 영역에서 가능한 한 일을 적게 하는 것이다.
- 과도한 동기화가 초래하는 진짜 비용은 경쟁하느라 낭비하는 시간, 즉 병렬로 실행할 기회를 잃고, 모든 코어가 메모리를 일관되게 보기 위한 지연시간이 진짜 비용이다.



## 가변 클래스 작성시 둘중 하나를 따르자.

- 클래스를 사용해야 하는 클래스가 외부에서 알아서 동기화하게 하자
- 동기화를 내부에서 수행해 스레드 안전한 클래스로 만들자

만일 선택하기 어렵다면 동기화하지 말고 문서에 **스레드 안전하지 않다**를 표기해주자

클래스 내부에서 동기화 하기로 했다면, **락 분할(lock splitting)**, **락 스트라이핑(lock striping)**, **비차단 동시성 제어(nonblocking concurrency control)**등 다양한 기법을 동원해 동시성을 높여줄 수 있다.

