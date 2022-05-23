## 장점

- 싱글턴임을 API에 나타낼 수 있다.

  ```java
  public class Elvis {
  	private static final Elvis INSTANCE = new Elvis();
  
  	private Elvis() {}
  	public static Elvis getInstance() { return INSTANCE;}
  }
  ```

  - 위 코드(정적 팩토리)의 장점

    - API를 변경하지 않고도, 싱글턴이 아니도록 변경할 수 있다.

      - ex) 호출하는 스레드별로 다른 인스턴스를 넘겨주게 할 수 있다. (관련 개념? 스프링의 로컬 스레드?)
      - 제네릭 싱글턴 팩토리

      ```java
      public class MetaElvis<T> {
      	
      	private static final MetaElvis<Object> INSTANCE = new MetaElvis<>();
      	private MetaElvis() {}
      
      	public static <T> MetaElvis<T> getInstance() { return (MetaElvis<T>) INSTANCE; }
      
      	public void say(T t) {
      		System.out.println(t);
      	}
      	
      
      	public static void main(String[] args) {
      		MetaElvis<String> elvis1 = MetaElvis.getInstance();
      		MetaElvis<Integer> elvis2 = MetaElvis.getInstance();
      
      		System.out.println(elvis1.equals(elvis2)); // true
      		System.out.println(elvis1 == elvis2); // false
      	}
      }
      ```

      - 정적 팩토리의 메소드 참조를 공급자(Supplier)로 사용할 수 있다

      ```java
      public interface Supplier<T> {
      	T get();
      }
      ```

- Enum 타입의 싱글턴 장점

  - 간결하고, 직렬화되고, 리플렉션 공격에서도 제2의 인스턴스가 생기는 것을 막아준다.
    - 단, 싱글턴이 Enum외에 클래스를 상속해야한다면, 이방법은 사용할 수 없다.

## 단점

- 이를 사용하는 클라이언트를 테스트하기가 어려워질 수 있다. → mock 구현으로 대체할 수 없다.

  - Interface 및 Mock 객체로 대체할 수 있다.

- Enum 타입이 아닌경우, 리플렉션으로 싱글톤이 꺠질 수 있다.

  - 아래와 같이 방어 코드를 작성할 수 있다.

  ```java
  public class Elvis {
  	private static final Elvis = INSTANCE = new Elvis();
  	private static boolean created;
  
  	private Elvis() {
  		if (created) {
  			throw new UnsupportedOperationException("can't be created by constructor.");
  		}
  
  		created = true;
  	}
  
  	public static Elvis getInstance() { return INSTANCE;}
  }
  ```

- 역직렬화시 싱글톤이 깨질 수 있다.

  ```java
  // @Override를 사용하면, 컴파일 에러가 아니다. (즉 오버라이딩이 아닌데, 해당 메소드가 사용이 된다.)
  // 리플렉션을 적용하는 듯 싶으나, 주석으로만 확인이 가능함.
  private Object readResolve() {
  	return INSTANCE;
  }
  ```