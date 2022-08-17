### 다른 타입이 적절하다면 문자열 사용을 피하라
- 문자열은 다른 값 타입을 대신하기에 적합하지 않다.
- 문자열은 열거타입, 혼합타입, 권한을 표한하기에 적합하지 않다.

### 문자열은 혼합 타입을 대신하기에 적합하지 않다.
```java
String compoundKey = className + "#" + i.next();
```
 - 위에 string 요소를 분리해서 사용해야된다면 파싱을 해야하여 복잡하며 오류 가능성도 생긴다.
 - 또한 equals, toString, CompareTo을 사용할수 없다.
 - 두 요소를 관리할수 있는 별도 클래스를 만들어 사용하자.

### 문자열은 권한을 표시하기에 적하지 않다.
```java
// 쓰레드 지역변수 설계 
// 자바 2이전에는 개발자가 직접 설계했고, 클라이언트 제공한 문자열로 지역변수를 식별
public class ThreadLocal {
    private ThreadLocal() { } 
    
    public static void set(String key, Object value);
    
    public static Object get(String key);
}
```
- 만약 두 클라이언트가 서로 같은 키를 사용하게 되면 둘다 문제가 생긴다.
- 악의적인 클라이언트라면 의도적으로 같은 키를 사용하여 다른 클라이언트 값을 획득 할수 있다.

```java
// 별도의 타입을 만들어 해결하자.
public class ThreadLocal {
    private ThreadLocal() {}
    public static class Key {
        key() {}
    }
    
    //위조 불가능한 고유 키를 생성한다.
    public static Key getKey() {
		return new Key();
    }
    
    public static void set(Key key, Object value);
    public static Object get(Key key);
}
```
- 리펙토링
```java
// 타입 안정성을 위해 제너릭 타입으로 변경
// set, get method는 static이 아니여도 된다.
public final class ThreadLocal<T> {
    public ThreadLocal();
    public void set(T value);
    public T get();
}
```

#### 정리
문자열을 잘못 사용하면 번거롭고, 덜 유연하고, 느리고, 오류 가능성도 크다.
더 적합한 타입이 있다며 해당 타입을 사용하자.