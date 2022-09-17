# 스레드 안전성 수준을 문서화하라

API문서에서 아무런 언급이 없으면, 그 클래스 사용자는 나름의 가정을 해야하며 만약 가정이 틀리다면, 동기화를 충분히 하지 못하거나, 지나치게 한 상태일 것이며 두 경우 모두 심각한 오류로 이어질 수 있다.



## synchronized 한정자

- 메서드 선언에 synchronized 한정자를 선언할지는 구현 이슈일 뿐 API에 속하지 않는다, 그러므로 메서드가 스레드 안전하다고 장담할 수 없다.
- javaDoc의 기본 옵션에서는 이러한 이유로 synchronized 한정자를 표기해주지 않는다.



## 클래스가 지원하는 스레드 안정성 수준을 명시하라

아래 목록은 스레드 안전성이 높은 순으로 나열한 내용이다.

- 불변(immutable): 상수와 같기에 외부 동기화도 필요 없다. `String`, `Long`, `BigInteger`이 대표적이다.
- 무조건적 스레드 안전(unconditionally thread-safe): 인스턴스는 수정될 수 있으나 내부에서 충실히 동기화 하기에, 외부에서는 별도의 동기화 없이 사용해도 안전하다. `AtomicLong`, `ConcurrentHashMap`이 여기에 속한다.
- 조건부 스레드 안전(conditionally thread-safe): 무조건적 스레드 안전과 같으나, 일부 메서드는 동시에 사용하려면 외부 동기화가 필요하다. `Collections.synchronized` 래퍼 메서드가 반환한 컬렉션들이 여기 속한다.
- 스레드 안전하지 않음(not thread-safe): 이 클래스의 인스턴스는 수정될 수 있으며, 동시에 사용하려면 외부 동기화 메커니즘으로 감싸야 한다. `ArrayList`, `HashMap`과 같은 기본 컬렉션이 여기 속한다.
- 스레드 적대적(thread-hostile): 모든 메서드 호출을 외부 동기화로 감싸더라도 멀티스레드 환경에서 안전하지 않다. 스레드 적대적으로 밝혀진 클래스나 메서드는 문제를 고쳐 재배포 하거나 deprecated API로 지정된다.

`@Immutable`, @ThreadSafe, `@NotThreadSafe` 어노테이션으로도 표기해줄 수 있다.



## 조건부 스레드 안전한 클래스는 문서화에 주의하라

- 어떤 순서로 호출할 때 외부 동기화가 필요한지, 그 순서로 호출하려면 어떤 락 혹은 락들을 얻어야 하는지 알려주어야 한다. (Collections.synchroniedMap이 좋은 예이다.)

  ```
  // synchronizedMap이 반환한 맵의 컬렉션 뷰를 순회하려면 반드시 그 맵을 락으로 사용해 수동으로 동기화하라.
  
  Map<K, V> m = Collections.synchronizedMap(new HashMap<>());
  Set<K> s = m.keySet(); // 동기화 블록 밖에 있어도 된다.
  ...
  synchronized(m) { // s가 아닌 m을 사용해 동기화해야 한다!
  	for(K key: s) {
  		key.f();
  	}
  }
  
  // 이대로 따르지 않으면 동작을 예측할 수 없다.
  ```

  

## 서비스 거부 공격(denial-of-service attack)을 막으려면 비공개 락 객체를 사용하라

- 클래스가 외부에서 사용할 수 있는 락을 제공하면 일련의 메서드 호출을 원자적으로 수행할 수 있다.
- 다만 클라이언트가 공개된 락을 오래 쥐고 놓치 않는 서비스 거부 공격을 수행할 수도 있다.
- 서비스 거부 공격을 막으려면 **synchronized 메서드 대신 비공개 락 객체를 사용하라.**
- 비공개 락 객체 관용구는 상속용으로 설계한 클래스에 특히 잘 맞는다.
  - 상속용 클래스에서 자신의 인스턴스를 락으로 사용한다면, 하위 클래스는 의도치 않게 기반 클래스의 동작을 방해할 수 있으며, 서로가 서로를 훼방놓는 상태에 빠진다.

```java
private final Object lock = new Object();

public void foo() {
	synchronized (lock) {
		...
	}
}
```

> lcok 객체를 final로 선언함으로 락 객체가 교체되는 일을 예방할 수 있으므로, 락 필드는 항상 final로 선언해라.



