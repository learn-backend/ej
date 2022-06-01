### 인터페이스는 구현하는 쪽을 생각해 설계하라
- 자바 8 이전에는 기존 구현체를 깨지 않고는 인터페이스에 메서드를 추가할수 없었다.
- 자바 8 부터 디폴트 메소드가 추가되었지만 완벽히 문제점이 해결된건 아니다.

#### 자바 8 Collection
```java
public interface Collection<E> extends Iterable<E> {
    default boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean result = false;
        for (Iterator<E> it = iterator(); it.hasNext(); ) {
            if (filter.test(it.next())) {
                it.remove();
                result = true;
            } 
        }
        return result;
    }
}
```
#### 위에 default 메소드는 모든 collection 구현체와 잘 어울러지는것은 아니다.
- APACHE.COMMONS.COLLECTIONS4.COLLECTION.SYNCHRONIZEDCOLLECTION
  - 클라이언트가 제공한 객체로 락을 거는 능력을 추가로 제공한다.
  - 모든 메서드에서 주어진 락 객체로 동기화한 후 내부 컬렉션 객체에 기능을 위임하는 래퍼 클래스
- removeIf 의 구현은 동기화에 대해 모르므로 락 객체를 사용할 수 없다.
   3.2.2 버전에서는 removeIf를 재정의하지 않고 있다.
- 4.4 버전에서는 removeIf를 재정의하고 있음.
```java
/**
* @since 4.4
*/
@Override
public boolean removeIf(Predicate<? super E> filter) {
   synchronized (lock) {
        return decorated().removeIf(filter);
     }
}
```
#### 정리
- 디폴트 메서드는 컴파일에 성공하더라도 기존 구현체에 런타임 오류를 발생시킬수 있다.
  - (디폴트 메서드로 새 메서드를 추가하는 일은 꼭 필요할때만 사용하자.)
- 디폴트 메서드가 있더라도 인터페이스를 설계할때는 세심한 주의를 기울여야한다.
- 새로운 인터페이스라면 반드시 테스트를 거치고 최소한 3가지 다른 방법으로 구현해보자.
- 물론 인터페이스를 릴리즈하고나서 수정할수도 있지만, 절대 그 가능성에 기대지 말자.

#### 참조 링크
- [APACHE.COMMONS.COLLECTIONS4.COLLECTION.SYNCHRONIZEDCOLLECTION](https://commons.apache.org/proper/commons-collections/apidocs/src-html/org/apache/commons/collections4/collection/SynchronizedCollection.html#line.193)