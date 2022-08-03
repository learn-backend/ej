# 공개된 API 요소에는 항상 문서화 주석을 작성하라



API를 올바른 문서화를 하려면 공개된 모든 클래스, 인터페이스, 메서드, 필드 선언에 문서화 주석을 달아야 한다. [How to Write Doc Comments](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html) 에 전반적인 내용이 서술되어 있으며, 해당 장에서는 웹페이지에 없는 내용을 설명한다.



### javaDoc Tags

공통적으로 아래 태그 사용시 마침표를 찍지 않는다.

- @param: 모든 매개변수에 대한 설명을 표현해주어야 한다.

- @return: 반환 타입이 void가 아닌경우를 표기해주어야 한다.

- @throws: 발생할 가능성이 있는 모든 예외를 표기해주어야 한다.

- {@code ...}: 태그로 감싼 내용을 코드용 폰트로 랜더링 해주며, 태그로 감싼 내용에 포함된 HTML 요소나 다른 javaDoc 태그를 무시해준다. (여러줄로 된 코드는 `<pre></pre>` 로 감싸주면 된다.)

  > ex: `<pre>{@code ... 코드 ...}</pre>`

- @implSpec: 상속용 클래스의 메서드를 올바로 재정의 하는법을 표기해주어야 한다.

  > java 11까지도 -tag "implSpec:a:Implementation Requirements: " 옵션을 켜주지 않으면 @implSpec 태그를 무시해버린다.

- {@literal ...}: <, >, &, HTML 메타문자를 포함해도 기본 문자열로 인식해준다.
- {@index ...}: Java 9에서부터 JavaDoc이 생성한 HTML에 문서의 검색 기능이 추가되었는데, 검색 키워드를 색인화 할 수 있다.
- {@inheritDoc ...}: 상위 타입의 문서화 주석 일부를 상속할 수 있다. (상위 타입의 주석을 그대로 사용할 수 있지만 몇가지 제약사항이 있을 수 있다. [오라클 공식문서](http://bit.ly/2vqmCzj))



요약 설명은 주석의 첫 문장부터, 첫 번째 마침표가 나오는 곳 까지를 요약 설명으로 판단한다. 

> 중간에 마침표가 있다면, {@literal}로 감싸주어라.

```java
/**
* A suspect, such as Colonel Mustard or {@literal Mrs. Peacock}.
*/
public class Suspect {...}

// java 10부터는 {@summery} 태그가 지원되어 아래와 같이 처리할 수도 있다.
/**
* {@summery A suspect, such as Colonel Mustard or Mrs. Peacock.}
*/
public enum Suspect {...}
```



### JavaDoc 작성 Rules

- 메서드용 문서화 주석에는 해당 메서드와 클라이언트 사이의 규약을 명료하게 기술해야 한다.
- 메서드와 생성자의 요약설명은 (주어가 없는)**동사구** 이어야 한다.
- 클래스, 인터페이스, 필드의 요약 설명은 **명사절** 이어야 한다.
- 제네릭 타입이나 제네릭 메서드를 문서화할 때는 **모든 타입 매개변수에 주석**을 달아야 한다.
- 열거 타입을 문서화할 때는 **상수들에도 주석**을 달아야 한다.
- 애너테이션 타입을 문서화 할 때는 **멤버들에도 모두 주석을 달아야 한다.**
- 패키지를 설명하는 문서화 주석은 `package-info.java` 파일에 작성한다.
- java 9부터 사용하는 모듈 기능을 사용한다면 `module-info.java`에 파일을 작성한다.
- **클래스 혹은 정적 메서드가 스레드 안전 수준을 반드시 API 설명에 포함해라.**
- 직렬화 할 수 있는 클래스라면 직렬화 형태도 API 설명에 기술해라.
- 아키텍처등 별도의 설명이 필요한 경우에는 링크를 제공해줘라.



javaDoc은 프로그래머가 자바독 문서를 올바르게 작성했는지 아래와 같은 기능을 제공해준다.

- java7 에서는 -Xdoclint 스위치를 켜주는 방법이 있으며 8부터는 기본으로 동작한다.
- checkstyle 같은 IDE 플러그인을 사용해볼 수도 있다.
- javaDoc이 생성한 HTML 파일을 HTML 유효성 검사기로 돌려볼 수도 있다.

**다만, 정말 잘 쓰인 문서인지 확인하는 유일한 방법은 자바독 유틸리티가 생성한 웹페이지를 읽어보는 길이다.**



