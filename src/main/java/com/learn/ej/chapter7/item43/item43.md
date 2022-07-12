# 람다보다는 메서드 참조를 사용하라



메서드 참조는 경우에따라 람다보다 더 간결하게 작성할 수 있다.



w. lambda

```java
map.merge(key, 1, (count, incr) -> count + incr);
```



w. method reference

```java
map.merge(key, 1, Integer::sum);
```





IDE는 람다를 메소드 참조로 대체하라고 권한다.

<img width="859" alt="CleanShot 2022-07-11 at 10 47 41@2x" src="https://user-images.githubusercontent.com/37217320/178173683-c27106ab-9867-457a-afde-cacdcbbaf7d7.png">

> 개인 적으로는 아래와 같은 방식으로 메서드 참조를 이용한다.
>
> 1. 익명 클래스 사용 (IDE Override 자동완성)
> 2. 람다 변환 (IDE 기능을 이용한 리펙토링)
> 3. 메소드 참조 변환 및 가독성 확인 (IDE 기능을 이용한 리펙토링)



다만 어떤 람다에서는 **매개변수의 이름 자체가 좋은 가이드가 될 수도 있으며, 람다가 더 읽기 쉬운 케이스도 있을 수** 있다.

```java
service.execute(GoshThisClassNameIsHumongous::action); // method reference

service.execute(() -> action()); // lambda
```





인스턴스 메소드를 참조하는 유형은 아래 두가지가 존재한다.

- 수신 객체를 특정하는 **한정적 인스턴스 메서드 참조**
- 수신 객체를 특정하지 않는 **비한정적 인스턴스 메서드 참조**



**한정적 인스턴스 메서드 참조**

- 근본적으로 정적 참조와 유사
- 함수 객체가 받는 인수와 참조되는 메서드가 받는 인수가 같은 경우에 사용된다.



**비한정적 인스턴스 메서드 참조**

- 함수 객체를 적용하는 시점에 수신 객체를 알려준다.



마지막으로 클래스 생성자를 가리키는 메서드 참조도 존재한다.





| 메서드 참조 유형    | 예                     | 같은 기능을 하는 람다                                   |
| ------------------- | ---------------------- | ------------------------------------------------------- |
| 정적                | Integer.parseInt       | str -> Integer.parseInt(str)                            |
| 한정적 (인스턴스)   | Instant.now()::isAfter | Instant then = Instant.now();<br />t -> then.isAfter(t) |
| 비한정적 (인스턴스) | String::toLowerCase    | str -> str.toLowerCase()                                |
| 클래스 생성자       | TreeMap<K, V>::new     | () -> new TreeMap<K, V>()                               |
| 배열 생성자         | int[]::new             | len -> new int[len]                                     |



**메서드 참조쪽이 짧고 명확하다면 메서드 참조를 쓰고, 그렇지 않을 때만 람다를 사용하라.**