# 변경 가능성을 최소화하라

### 불변 클래스
-  인스턴스 내부 값을 수정 못함
-  생성 시점에 초기화 된 값 소멸 될 때까지 변경 안됨
-  일반 클래스에 비해 설계, 구현, 사용 쉽고 안전함

###불변 클래스를 만들기 위한 규칙
1. 객체의 상태를 변경하는 메서드를 제공하지 않는다
   - setter
2. 클래스를 확장할 수 없도록 한다 (상속을 막는다)
   - 클래스를 final로 선언
   - 모든 생성자를 private or package-private으로 만들고 public 정적 팩토리 제공
   - 두번째 방법이 더 유연함 (ex. Boolean 캐싱)
3. 모든 필드를 final로 선언한다
   - 인스턴스에 대한 동기화 처리 없이 다른 스레드에서 문제없이 처리
4. 모든 필드를 private으로 선언한다
   - 가변객체를 클라이언트가 직접 접근하여 수정하는 것을 막음
   - public final 보다 유연함
5. 자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다
   - 클라이언트에서 인스턴스 내에 가변 객체의 참조를 얻지 못하게 해야함
   - 생성자, getter, readObject 등 메서드에서 방어적 복사해야함

```java
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE, staticName="of")
public class Person {
    private final String name;
    private final String gender;
    private final MBTI mbti;
    
    public getMbti() {
        return new MBTI.of(this.mbti);
    }
}
```
 
###함수형 프로그래밍
- 피연산자에 함수를 적용해 그 결과를 반환하지만, 피연산자 자체는 그대로인 프로그래밍 패턴
- 피연산자가 불변이므로 더 안전함

###불변 객체
-  불변 객체는 근본적으로 스레드 안전하여 따로 동기화 할 필요가 없다
   - 안심하고 공유가 가능하므로 최대한 재활용
    - 자주 사용되는 인스턴스를 정적 팩터리로 제공
    - 메모리 사용량, 가비지 컬렉션 비용 줄임
-  방어적 복사 필요 없음
    - clone 필요없음
    - 복사해봐야 똑같은 것이므로 복사 필요 없다
      - 안티패턴 : String 복사 생성자 
-  불변 객체는 자유롭게 내부 데이터를 공유가능
    - 예시 만들기
-  객체를 만들 때 구성요소로 사용하기 좋음
    - 맵의 키
    - 집합의 원소
    - 불변임이 보장되므로 신경쓸일이 줄어듬
-  실패 원자성 제공
    - 예외가 발생하더라도 객체가 변하지 않음
-  값이 다르면 반드시 독립된 객체로 만들어야 함
    - 비용적 측면에서 손해
    - 가변 동반 클래스(ex. StringBuilder)로 보완이 가능

###정리
- getter가 있다고 setter 만들지 말자
    - lombok 주의
- 불변으로 만들 수 없는 클래스라도 변경할 수 있는 부분을 최소한으로 줄이자
- 다른 합당한 이유가 없다면 모든 필드는 private final이어야 한다
- 생성자는 불변식 설정이 모두 완료된, 초기화가 완벽히 끝난 상태의 객체를 생성해야 한다