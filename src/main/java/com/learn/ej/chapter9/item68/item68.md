# 일반적으로 통용되는 명명 규칙을 따르라

자바 플랫폼은 명명규칙이 [JLS 6.1](https://docs.oracle.com/javase/specs/jls/se7/html/jls-6.html)에 잘 기술되어 있다. 이 규칙들은 특별한 이유가 없는 한 따르는 것이 좋다.

규칙을 지치지 않으면 가독성이 떨어지며 다른 뜻으로 오인할 수도 있어 오류를 발생할 수 있다.



## Package (철자 규칙)

- 패키지와 모듈 이름은 각 요소를 점(.) 으로 구분하여 계층적으로 짓는다.
- 모두 소문자 알파벳 혹은 숫자로 이뤄진다. (숫자는 매우 드물다.)
- 조직 바깥에서도 사용될 패키지라면 패키지명을 조직의 도메인 이름을 역순으로 사용한다.
  - ex) google.com -> `com.google.project`
- 표준 라이브러리와 선택적 패키지들은 각각 java와 javax로 시작한다.
- 각 요소는 일반적으로 8자 이하의 짧은 단어로 한다. 
  - ex) utilities 보다는 utils 처럼 의미가 통하는 약어를 추천한다.
- 여러 단어로 구성된 이름이라면 awt와 같이 각 단어의 첫글자만 따서 써도 좋다.
- 많은 기능을 제공하는 경우에는 계층을 나눠 더 많은 요소로 구성해도 좋다. (subpackage)



## Class & Interface & Method.. (철자 규칙)

- 클래스와 인터페이스의 이름은 하나 이상의 단어로 이뤄지며, 각 단어는 대문자로 시작한다. (UPPER_CAMEL_CASE)

- 여러 단어의 첫 글자만 딴 약자나 max, min 처럼 통용되는 줄임알이 아니면 단어를 줄여쓰지 않도록 한다.

- 약자의 경우 첫글자만 대문자로 하는 사람들이 더 많다.

  - ex) HttpUrl - HTTPURL

- 메서드의 필드명은 첫글자를 소문자로 사용하며 외에는 클래스 명명규칙과 같다.(LOWER_CAMEL_CASE)

- 상수필드의 단어는 모두 대문자로 사용하며, 단어 사이는 밑줄로 구분한다.

  

타입 매개변수의 이름은 보통 한문자로 표현하며 아래와 같은 규칙을 가진다.

- T: 임의의 타입에 사용한다.
- E: 컬렉션 원소의 타입에 사용한다.
- K: 맵의 키에 사용한다.
- V: 맵의 값에 사용한다.
- X: 예외에 사용한다.
- R: 메소드 반환 타입에 사용한다.
- T, U, V or T1, T2, T3: 임의 타입 시퀀스에 사용한다.



## 문법 규칙

문법 규칙은 철자 규칙에 비교하면 더 유연하고 논란도 많다.

- 객체를 생성할 수 있는 클래스의 이름은 단순 명사나 명사구를 이용한다.
  - ex) `Thread`, `PriorityQueue`, `ChessPiece` ...
- 객체를 생성할 수 없는 클래스의 이름은 보통 복수형 명사로 짓는다.
  - ex) `Collectors`, `Collections` ...
- 인터페이스 이름은 클래스와 똑같이 짓거나 able 혹은 ible로 끝나는 형용사로 짓는다.
  - ex) `Runnable`, `Iterable`, `Accessible` ...
- 애너테이션은 워낙 다양하게 활용되어 지배적 규칙 없이 다양하게 활용된다.
  - ex) `BindingAnnotation`, `Inject` ...
- 어떤 동작을 수행하는 메서드의 이름은 동사나, 동사구로 짓는다.
  - ex) `append`, `drawImage` ...
- boolean을 반환하는 메서드는 보통 is나 (드물게) has로 시작한다.
  - ex) `isDigit`, `isProbablePrime`, `isEmpty`, `hasSiblings` ...
- 인스턴스의 속성을 반환하는 메서드의 이름은 보통 명사, 명사구 혹은 get으로 시작하는 동사구로 짓는다.
  - ex) `size`, `hashCode`, `getTime` ...
- get으로 시작하는 형태는 JavaBeans 명세에 뿌리를 두며, 게터와 세터 모두 제공할때 적합한 규칙이다.
  - ex) `getAttribute`, `setAttribute` ...
- toType은 객체의 타입을 바꿔서 다른 타입의 또 다른 객체를 반환하는 인스턴스 메서드 형태이다.
  - ex) `toArray`, `toString` ...
- asType은 객체의 내용을 다른 뷰로 보여줄때 주로 사용하는 형태이다.
  - ex) `asType`, `asList` ...
- typeValue는 객체의 값을 기본 타입으로 반환하는 메소드 이름 규칙이다.
  - ex) `intValue` ...
- 정적 팩터리의 이름은 다양하지만 `from`, `of`, `valueOf`, `instance`, `getInstance`, `newInstance`, `getType`, `newType`을 흔히 사용한다.



필드 이름에 관한 문법은 클래스, 인터페이스, 메서드 이름에 비해 덜 명확하고 덜 중요하다.

- API 설계를 잘 했다면 필드가 직접 노출될 일이 거의 없기 때문이다.
- boolean 타입은 접근자 메서드에서 앞 단어를 뺀 형태이다.
  - ex) `initaialized`, `composite` ...
- 다른타입의 필드는 명사나 명사구를 사용한다.
  - ex) `height`, `digits`, `bodyStyle` ...



> 표준 명명 규칙을 체화하여 자연스럽게 나오도록 하자.