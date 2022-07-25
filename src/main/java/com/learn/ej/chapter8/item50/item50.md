### 적시에 방어적 복사본을 만들라
 - 자바는 안전한 언어다.
   - C, C++ 같은 언어에서 보이는 버퍼 오버런, 배열 오버런, 와일드 포인터 같은 메모리 충돌 오류에서 안전하다.
   - 하지만 다른 클래스로부터의 침범을 노력없이 다 막을수 있는것은 아니다.
 
### 불변식을 지키지 못한 클래스
```java
// 기간을 표현하는 클래스
public final class Period {
    private final Date start;
    private final Date end;

    /**
     * @param  start 시작 시각
     * @param  end 종료 시각. 시작 시각보다 뒤여야 한다.
     * @throws IllegalArgumentException 시작 시각이 종료 시각보다 늦을 때 발생한다.
     * @throws NullPointerException start나 end가 null이면 발생한다.
     */
    public Period(Date start, Date end) {
        if (start.compareTo(end) > 0)
            throw new IllegalArgumentException(
                    start + "가 " + end + "보다 늦다.");
        this.start = start;
        this.end   = end;
    }

    public Date start() {
        return start;
    }
    public Date end() {
        return end;
    }

    public String toString() {
        return start + " - " + end;
    }

    


}
```
### 인스턴스 내부 공격
```java
        Date start=new Date();
        Date end=new Date();
        Period period=new Period(start,end);
        // 인스턴스 내부 메서드 수정
        period.end().setMonth(1);

```
 - 생성자에서 받은 가변 매개변수 각각 방어복사해야한다.
```java
//매개변수의 방어적 복사본을 만들어 방어처리 
    public Period(Date start, Date end) {
        // 순서가 어색해보이지만 멀티쓰레드 환경이라면 원본 객체의 유효성을 검사한후  복사본을 만드는 
        // 찰라에 다른 스레드가 원본 객체를 수정할수 있기 때문에 아래 순서로 처리한다.
        this.start = new Date(start.getTime());
        this.end   = new Date(end.getTime());

        if (this.start.compareTo(this.end) > 0)
            throw new IllegalArgumentException(
                    this.start + "가 " + this.end + "보다 늦다.");
    }
``` 
 - 접근자 메서드가 내부의 가변 정보를 드러내기떄문에 아직도 변경이 가능하다.
```java
        // 공격을 막기위해 가변 필드의 방어적 복사본을 반환하도록 처리
public Date start() {
        return new Date(start.getTime());
        }

public Date end() {
        return new Date(end.getTime());
        }
```

### 정리
 - 방어적 복사는 성능 저하가 발생하며 항상 쓸수있는 조건이 아니다.
 - 호출자가 컴포넌트의 내부를 수정하지 않는다고 확신되면 방어적 복사를 생략하는 것도 고려해볼만하다.
 - 방어적 복사의 생략은 그 영향이 오직 호출한 클라이언트에만 해댱될때로 한정해야하며 명확히 문서화할 필요가 있다.