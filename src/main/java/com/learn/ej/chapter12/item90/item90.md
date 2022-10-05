### 직렬화된 인스턴스 대신 직렬화 프록시 사용을 검토하라
- Serializable을 구현하기로 결정한 순간 언어의 정상 메커니즘인 생성자 이외의 방법으로 인스턴스를 생성할 수 있게 된다.
  - 문제점을 보완해줄 기법으로 프록시 패턴이 있다.

### 직렬화 프록시 패턴
 - 복잡하지 않은 구조
 - 일관성 검사, 방어적 복사도 필요 없다

### 직렬화 프록시 예제
```java
private static class SerializationProxy implements Serializable {
  private final Date start;
  private final Date end;

  SerializationProxy(Period p) {
    this.start = p.start;
    this.end = p.end;
  }

  private static final long serialVersionUID = 
      452376890235478394258L; // 아무값이나 상관없다.
}
```
```java
// 바깥 클래스에 writeReplace 메서드를 추가
private Object writeReplace() {
        return new SerializationProxy(this);
        }
```
- 이 메서드는 바깥 클래스의 인스턴스 대신 SerializationProxy의 인스턴스를 반환하게 하는 역할을 한다
- writeReplace 덕분에 직렬화 시스템은 결코 바깥 클래스의 직렬화된 인스턴스를 생성해낼 수 없다.

```java
// 바깥 클래스와 논리적으로 동일한 인스턴스를 반환하는 readResolve 메서드를 SerializationProxy 클래스에 추가한다
private Object readResolve() {
        return new Period(start,end); // public 생성자를 사용한다.
        }
```
### EnumSet의 직렬화 프록시
```java
private static class SerializationProxy <E extends Enum<E>>
    implements Serializable {
      private final Class<E> elementType;
      private final Enum<?>[] elements;

      SerializationProxy(EnumSet<E> set) {
        elementType = set.elementType;
        elements = set.toArray(new Enum<?>[0]);
      }

      private Object readResolve() {
        EnumSet<E> result = EnumSet.noneOf(elementType);
        for (Enum<?> e : elements)
          result.add((E)e);
        return result;
      }

      private static final long serialVersionUID = 
          4578194361234063217489L;
    }
```
### 직렬화 프록시 패턴의 한계
- 클라이언트가 멋대로 확장할 수 있는 클래스에는 적용할 수 없다.
- 객체 그래프에 순환이 있는 클래스에도 적용할 수 없다.
- Period를 예제 기준 방어적 복사 때보다 14% 느렸다.

### 정리
- 제3자가 확장할수 없는 클래스라면 가능한 한 직렬화 프록시 패턴을 사용하자.

