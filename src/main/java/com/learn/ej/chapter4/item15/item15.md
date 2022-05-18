# 클래스와 멤버의 접근 권한을 최소화하라





내부 구현 정보를 외부 컴포넌트로부터 얼마나 잘 숨겼느냐에 따라, **어설프게 설계된 컴포넌트와 잘 설계된 컴포넌트로 구분된다.**



### **캡슐화의 장점**

- 시스템 개발 속도를 높인다. (여러 컴포넌트를 병렬로 개발할 수 있기에)
- 시스템 관리 비용을 낮춘다. (각 컴포넌트별로 파악하면 되며, 교체 비용이 적어진다.)
- 캡슐화가 성능을 높여주지는 않지만, 성능 최적화에 도움을 준다. (내부 로직만 최적화가 가능하다.)
- 소프트웨어 재사용성을 높인다. (클라이언트는 코드를 재사용 할 수 있고 외부에 의존하지 않는다면, 변경 영향도가 적다.)
- 큰 시스템을 제작하는 난이도를 낮춰준다. (시스템 전체가 완성되지 않은 상태에서도 개별 컴포넌트의 동작을 검증할 수 있다.)



**기본 원칙**

> 모든 클래스와 멤버의 접근 가능성을 가능한 한 좁혀야 한다.

**Java에서 제공하는 접근제어자**

- private: 멤버를 선언한 톱레벨 클래스에서만 접근할 수 있다.
- package-private: 멤버가 소속된 패키지 안의 모든 클래스에서 접근할 수 있다. (default)
- protected: package-private를 호팜하며, 이 멤버를 선언한 클래스의 하위 클래스에서도 접근할 수 있다.
- public: 모든 곳에서 접근할 수 있다.



Serializable을 구현할 클래스에서는 private 필드들도 의도치 않게 공개될 수 있다. (item 86, 87)

> Serializable을 구현하면 직렬화된 바이트 스트림 인코딩도 하나의 공개 API가 된다. 그 직렬화 형태를 다른 클래스에서 사용한다면, 영원히 지원해야 한다. 뒤늦게 클래스 내부 구현을 손보면 원래의 직렬화 형태와 달라지게 되며 한쪽은 구버전 인스턴스를 직렬화 하고, 다른 쪽은 신버전 클래스로 역직렬화 하기에, 버전 이슈가 발생할 수 있다. - (item 86중 일부)



**상위 클래스의 메서드를 재정의할 때는 상위 클래스에서보다 좁게 설정할 수 없다.**

> 상위 클래스의 인스턴스는 하위 클래스의 인스턴스로 대체해 사용할 수 있어야 한다. (리스코프 치환 원칙)



public 클래스의 인스턴스 필드는 되도록 public이 아니어야 한다.

- public 가변 필드를 갖는 클래스는 일반적으로 Thread-safety 하지 않다.

  > 이부분은 Getter / Setter 도 동일할 것 같은데, 내부적으로 Thread-safety 하게 구현할 수 있으므로 위와 같이 설명하지 않았을까 싶다.

- 내부 구현을 바꾸고 싶어도 변경이 어렵다. (클라이언트에 영향이 간다.)

- 구성요소로써의 상수라면 public static final 필드로 공개해도 좋다.

  컨벤션: https://google.github.io/styleguide/javaguide.html#s5.2.4-constant-names



단 public static final 배열은 역시 final 제약조건이 깨질 수 있다.

**해결 방법**

- 불변 리스트로 만들자.

  ```java
  private static final Thing[] PRIVATE_VALUES = { ... };
  private static final List<Thing> VALUES = Collections.unmodifiableList(Arrays.asLsit(PRIVATE_VALUES));
  ```

- 배열을 private로 구성하고 방어적 복사본을 반환하는 public 메소드를 만들자

  ```java
  private static final Thing[] PRIVATE_VALUES = { ... };
  private static final Thing[] values() {
  	return PRIVATE_VALUES.clone();
  }
  ```



TODO 추가 module 개념

Java 9부터 지원 등등..



### module

- Java 9 부터 지원
- 패키지들의 묶음이라고 보면 됨



**적용 테스트**

- module은 패키지 root에 위치해야 한다. (아래와 같이 서브에 등록해도 되는지 테스트 해보니 컴파일 에러가 난다.)

  <img width="499" alt="CleanShot 2022-05-18 at 18 38 37@2x" src="https://user-images.githubusercontent.com/37217320/169009156-9c0a1a4a-7808-4f18-a21b-09f68dc47473.png">

  

  <img width="751" alt="CleanShot 2022-05-18 at 18 37 13@2x" src="https://user-images.githubusercontent.com/37217320/169008932-6461494e-d554-4d11-b616-ab9ba2b6edfc.png">

- 파일명은 `module-info.java`를 사용해야 한다. (Java 파일명은 원래 '-' 가 불가함)

- 타 프로젝트의 라이브러리 & 모듈을 사용할때 영향이 있는 것 같다.
  예시로 lombok을 찾아오지 못했으며, 아래와 같이 설정해주어야 롬복이 정상적으로 불러와 진다.

  **[모듈 미지정]**

  <img width="694" alt="CleanShot 2022-05-18 at 18 41 51@2x" src="https://user-images.githubusercontent.com/37217320/169009807-263b6a2f-3303-4566-9839-3b5d92d3d086.png">

  **[모듈 지정]**

  ```java
  module com.learn.ej {
      requires lombok;
  }
  
  ```

  <img width="623" alt="CleanShot 2022-05-18 at 18 45 24@2x" src="https://user-images.githubusercontent.com/37217320/169010451-c755c134-2a63-4953-a14d-f43f52a21893.png">

  

  다만 아래와 같은 가정을 해보았는데 아래 예시에서는 동작을 하지 않았다.

  - root 프로젝트 단위로 동작하는 건가 싶다.



1. lombok 모듈을 a 패키지로 만 지정

   ```java
   module com.learn.ej.chapter4.item15.example.a {
       requires lombok;
   }
   ```

2. b 패키지에서 lombok 사용

   ```java
   package com.learn.ej.chapter4.item15.example.b;
   
   
   import lombok.RequiredArgsConstructor;
   
   @RequiredArgsConstructor
   public class AirPlane {
   
   }
   ```



3. **[결과] 프로젝트 단위로 일괄 적용이 되는것인가 싶다.**
   기대 결과: lombok을 사용할 수 없어야 한다.
   실제 결과: lombok을 사용할 수 있다.



조금 더 찾아보니, 모듈에 작성하는 이름은 **패키지 경로가 아니라 정말 모듈명** 이다.

```java
module MyModuleName {
    requires lombok;
}
```



### 핵심 정리

- 꼭 필요한 것만 골라 최소한의 public API를 설계하자
- 의도치 않게 API 가 공개로 되는일이 없게 주의하라
- public static final 필드가 참조하는 객체가 불변인지 확인해라