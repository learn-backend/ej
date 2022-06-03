# 로 타입은 사용하지 말라



**제네릭 타입**

- 클래스와 인터페이스 선언에 타입 매개변수가 사용된 것

- Java 1.5 이후부터 지원

- **type safety를 컴파일 시점에 보장**

  ```java
  public class List<E> {
  	...
  }
  ```

  

**로 타입** 

- 제네릭 타입에서 타입 매개변수를 전혀 사용하지 않은 것

  ```java
  List list = new ArrayList<>();
  ```



**로타입 단점**

- 제네릭의 장점은 컴파일 시점에 type safety를 보장해줄 수 있다. 그러나 로 타입을 사용하면, 장점이 깨진다.

```java
List list = new ArrayList<>();

list.add("String");
list.add(10);

for (Object item : list) {
  Integer item1 = (Integer) item; // 컴파일은 가능하나, 런타임시 오류가 발생한다.
}
```

​	제네릭을 사용하면 아래와 같이, 컴파일 시점에 오류를 발견할 수 있다.

<img width="679" alt="CleanShot 2022-06-02 at 14 32 48@2x" src="https://user-images.githubusercontent.com/37217320/171559744-10b6444a-36ad-4f30-ae2b-4848edca6e31.png">







**로타입이 남아있는 이유**

- 자바가 제네릭을 받아들이기 까지 거의 10년이 걸렸다고 한다, 그렇다보니 하위 호환성을 지원하기 위해 남아 있다고 한다.





**비한정적 와일드 카드**

- 실제 타입 매개변수가 무엇인지 신경쓰고 싶지 않다면 물음표(?)를 사용하라

  ```java
   List<?> list = new ArrayList<>();
  ```

- 로타입은 아무 원소나 넣을 수 있으나, 비한정적 와일드 카드를 사용하면 (null 이외에는) 어떠한 원소도 넣을 수 없다.

  <img width="637" alt="CleanShot 2022-06-02 at 14 37 52@2x" src="https://user-images.githubusercontent.com/37217320/171560305-fecc2432-3ff8-4eb2-a3b3-caab97aaf68c.png">

  ```java
  error: incompatible types: int cannot be converted to CAP#1
          list.add(10);
                   ^
    where CAP#1 is a fresh type-variable:
      CAP#1 extends Object from capture of ?
  ```

  



**로타입을 사용해야 하는 경우 (예외 케이스)**

- class 리터럴에는 로 타입을 사용해야 한다. (Java에서 지원하지 않는다.)

  <img width="364" alt="CleanShot 2022-06-02 at 14 43 53@2x" src="https://user-images.githubusercontent.com/37217320/171561045-149043ab-a843-4af7-a378-a03681dbd1a7.png">

- instanceof 연산자를 사용하는 경우에는 로 타입을 사용해야 한다.

  > 제네릭은 컴파일 시점에만 유효하고 런타임 시점에는 타입 정보가 지워진다. 그렇다 보니 instanceof에서 로 타입이든, 비한정적 와일드카드 타입이든 동일하게 동작한다.
  >
  > 제네릭의 꺽쇠괄호와 물음표는 불필요하게 코드만 지저분하게 만드므로, 깔끔한 방식으로 사용하도록 된 것으로 보인다.

<img width="807" alt="CleanShot 2022-06-02 at 14 46 05@2x" src="https://user-images.githubusercontent.com/37217320/171561329-cb4c989e-dc36-4621-a088-05909a97d2ca.png">

컴파일된 클래스는 아래와 같다.

<img width="779" alt="CleanShot 2022-06-02 at 14 47 41@2x" src="https://user-images.githubusercontent.com/37217320/171561510-57c09972-e561-44c8-bdd1-42a49a66eae0.png">



> 제네릭이 가지는 type safety 함을 가져가기 위해, 로타입은 사용하지 말라.