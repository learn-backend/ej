### 인스턴스 수를 통제해야 한다면 readResolve보다는 열거 타입을 사용하라

### readResolve가 수행되기 전 역직렬화된 객체의 참조를 공격하는 케이스
- transient 선언을 하지 않은 케이스
  - transient로 선언하지 않을 경우, 역직렬화 과정에서 역직렬화된 인스턴스의 참조를 가져올 수 있다는 문제가 발생

```java
public class Elvis implements Serializable {
    public static final Elvis INSTANCE = newElvis();

    private Elvis() {
    }

    private String[] favoriteSongs = {"Hound Dog", "Heartbreak Hotel"};

    public void printFavorites() {
        System.out.println(Arrays.toString(favoriteSongs));
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
```
```java
// 도둑 클래스
public class ElvisStealer implements Serializable {
    static Elvis impersonator;
    private Elvis payload;

    private Object readResolve() {
        // resolve되기 전의 Elvis 인스턴스의 참조를 저장한다. 
        impersonator = payload;
        // favoriteSongs 필드에 맞는 타입의 객체를 반환한다. 
        return new String[]{"A Fool Such as I"};
    }

    private static final long serialVersionUID = 0;
}
```
```java
// 싱글턴 객체를 2개 생성하는 프로그램
public class ElvisImpersonator {
  // 진짜 Elvis 인스턴스로는 만들어질 수 없는 바이트 스트림
  private static final byte[] serializedForm = {
          -84, -19, 0, 5, 115, 114, 0, 20, 107, 114, 46, 115,
          101, 111, 107, 46, 105, 116, 101, 109, 56, 57, 46, 69,
          108, 118, 105, 115, 98, -14, -118, -33, -113, -3, -32,
          70, 2, 0, 1, 91, 0, 13, 102, 97, 118, 111, 114, 105, 116,
          101, 83, 111, 110, 103, 115, 116, 0, 19, 91, 76, 106, 97,
          118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110,
          103, 59, 120, 112, 117, 114, 0, 19, 91, 76, 106, 97, 118,
          97, 46, 108, 97, 110, 103, 46, 83, 116, 114, 105, 110, 103,
          59, -83, -46, 86, -25, -23, 29, 123, 71, 2, 0, 0, 120, 112,
          0, 0, 0, 2, 116, 0, 9, 72, 111, 117, 110, 100, 32, 68, 111,
          103, 116, 0, 16, 72, 101, 97, 114, 116, 98, 114, 101, 97, 107,
          32, 72, 111, 116, 101, 108
  };

  public static void main(String[] args) {
    // ElvisStealer.impersonator를 초기화한 다음,
    // 진짜 Elvis(즉 Elvis.INSTANCE)를 반환한다.
    Elvis elvis = (Elvis) deserialize(serializedForm);
    Elvis impersonator = ElvisStealer.impersonator;

    elvis.printFavorites(); // [Hound Dog, Heartbreak Hotel]
    impersonator.printFavorites(); // [A Fool Such as I]
  }
}
```
1. favoriteSongs 라는 non-transient 참조 필드가 있다
2. 바이트 코드에서 Elvis의 favoriteSongs 부분을 ElvisStealer 인스턴스로 교체
3. readResolve를 호출할 때 Elvis(new).favoriteSongs = ElvisStealer(new).readResolve() 처럼 코드가 실행
4. Elvis 역직렬화를 할 때 favoriteSongs이 ElvisStealer를 참조하고 있기 때문에 ElvisStealer의 readResolve 코드를 호출
5. ElvisStealer의 readResolve에서 반환하는 String[]이 반환


### 열거 타입
- 직렬화 가능한 인스턴스 통제 클래스를 열거 타입을 사용하면 선언한 상수 외의 다른 객체는 존재하지 않음을 자바가 보장해준다.
```java
public enum Elvis {
    INSTANCE;
    private String[] favoriteSongs = {"Hound Dog", "Heartbreak Hotel"};

    public void printFavorites() {
        System.out.printtn(Arrays.toString(favoriteSongs));
    }
}
```

### 정리
- 불변식을 지키기 위해 인스턴스를 통제해야할때는 열거타입을 사용하자.
- 컴파일 타임에  어떤 인스턴스들이 있는지 알 수 없을 때는 readResolve 메소드를 작성해야한다.
  - 클래스에서 모든 참조 타입 인스턴스 필드를 transient로 선언해야한다.