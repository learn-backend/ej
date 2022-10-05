### 커스텀 직렬화 형태를 고려해보라
- 고민후 괜찮다고 판단할때만 기본 직렬 형태를 사용하자.
  - 유연성, 성능, 정확성 측면에서 신중히 고민한 후 합당한 경우에 사용

### 기본 직렬화 형태에 적합한 케이스
- 객체의 물리적 표환과 논리적 내용이 같은 케이스
```java
public class Name implements Serializable {
    /**
     * 성, null이 아니어야 함
     * @serial
     */
    private final String lastName;

    /**
     * 이름, null이 아니어야 함
     * @serial
     */
    private final String firstName;

    /**
     * 중간 이름, 중간 이름이 없는 경우 null
     * @serial
     */
    private final String middleName;
    
}
```
- 이름은 이름, 성, 중간 이름이라는 3개의 문자열로 구성되고, 각 필드는 논리적 내용을 그대로 반영한다.
- 기본 직렬화 형태가 적합하다고 결정했더라도 불변식 보장과 보안을 위해 readObject 메서드를 제공해야 할 때가 많다.
  - readObject 메서드는 lastName과 firstName 필드는 null이 아님을 보장해야함.

### 기본 직렬화 형태에 적합하지 않는 케이스
- 논리적으로 일련의 문자열을 표현하고, 물리적으로는 문자열들을 이중 연결리스트로 연결한 케이스
```java
public final class StringList implements Serializable {
    private int size = 0;
    private Entry head = null;

    private static class Entry implements Serializable {
        String data;
        Entry next;
        Entry previous;
    }

    ...
}
```
- 공개 API가 현재의 내부 표현 방식에 영구히 묶임
  - 앞의 코드에서 StringList.Entry가 공개 API가 되는데 다음 릴리즈에서 내부 표현 방식을 바꾸더라도 
  - StringList는 여전히 연결 리스트로 표현된 입력도 처리할 수 있어야 한다.
- 너무 많은 공간 차지
  - 내부 구현에 해당되어 포함할 가치가 없는 정보까지 담는 경우, 직렬화 형태가 너무 커져서 디스크에 저장하거나 네트워크 전송 시 속도가 느려진다.
- 시간이 너무 많이 걸림
  - 직렬화 로직은 객체 그래프의 위상에 관한 정보가 없기 때문에 그래프를 직접 순회할 수 밖에 없다.
- 스택 오버플로우 위험
  - 기본 직렬화 과정은 객체 그래프를 재귀 순회하므로 스택오버플로우 위험이 있다.

### 합리적인 커스텀 직렬화 형태로 변경
```java
public final class StringList implements Serializable {
    private transient int size = 0;
    private transient Entry head = null;

    // 이제는 직렬화되지 않는다.
    private static class Entry {
        String data;
        Entry next;
        Entry previous;
    }

    // 지정한 문자열을 이 리스트에 추가한다.
    public final void add(String s) { ... }

    /**
     * {@code StringList} 인스턴스를 직렬화한다.
     *
     * @serialData 이 리스트의 크기(포함된 문자열의 개수)를 기록한 후
     * ({@code int}), 이어서 모든 원소를(각각은 {@code string}) 순서대로 기록한다.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(size);

        // 모든 원소를 올바른 순서로 기록한다.
        for (Entry e = head; e != null; e = e.next)
            s.writeObject(e.data);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        int numElements = s.readInt();

        // 모든 원소를 읽어 이 리스트에 삽입한다.
        for (int i = 0; i < numElements; i++)
            add((String) s.readObject());
    }

    ...
}
```
- 물리적 상세 표현은 배제한하고 논리적인 구성만 담는다.
- 기본 직렬화를 사용한다면 transient 필드들은 역직렬화될 때 기본값으로 초기화됨을 잊지 말아야 한다.
- 기본 직렬화를 수용하든 하지 않든 defaultWriteObject 메서드를 호출하면 transient로 선언하지 않은 모든 인스턴스 필드가 직렬화된다. 따라서 transient로 선언해도 되는 인스턴스 필드에는 모두 transient 한정자를 붙여야 한다.
  - 해당 객체의 논리적 상태와 무관한 필드라고 확신할때만 transient 한정자 생략할수 있다.

### 동기화
- 기본 직렬화 사용 여부와 상관없이 객체의 전체 상태를 읽는 매서드에 적용해야 하는 동기화 메커니즘을 직렬화에도 적용해야 한다.
```java
// 기본 직렬화를 사용하는 동기화된 클래스를 위한 writeObject 매서드
private synchronized void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject() ;
}
```
- 직렬화 가능 클래스 모두에 직렬버전 UID를 명시적으로 부여하자.
```java
private static final long serialVersionUID = <무작위 long>;

```
- 이렇게 하면 직렬 버전 UID가 일으키는 잠재적인 호환성 문제가 사라지고, 성능도 좋아진다.
- 직렬 버전 UID를 명시하지 않으면 런타임에서 이 값을 생성하느라 추가 연산을 수행한다.

### 정리
- 클래스를 직렬화하기로 했다면 어떤 직렬화 형태를 사용할지 고민해보자.
- 자바의 기본 직렬화 형태는 객체를 직렬화한 결과가 객체의 논리적 표현에 부합할 때만 사용하고
- 그렇지 않은 경우 객체를 적절히 설명할 수 있는 커스텀 직렬화 형태를 설계하자.