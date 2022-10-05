### readObject 메서드는 방어적으로 작성하라
- 아이템 50에서 불변인 날짜 범위 클래스를 만드는데 가변인 Date 필드를 이용함.
- 불변식을 지키기 위한 코드로 방어적 복사를 하느라 코드가 상당히 길어졌다.
```java
// 방어적 복사를 사용하는 불변 클래스
public final class Period {
    private final Date start;
    private final Date end;
    
    /**
     * @param start 시작 시각
     * @param end 종료 시각; 시작 시각보다 뒤여야 한다.
     * @throws IllegalArgumentException 시작 시각이 종료 시각보다 늦을 때 발생한다.
     * @throws NullPointerException start나 end가 null이면 발행한다.
     */
    public Period(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        if(this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException(start + "가 " + end + "보다 늦다.");
        }
    }
    
    public Date start() { return new Date(start.getTime()); }
    public Date end() { return new Date(end.getTime()); }
    public String toString() { return start + "-" + end; }
}
```
- 이 클래스를 직렬한다고 생각해보자?
  - implements Serializable 추가하면 될거 같지만 이렇게하면 불변식을 보장하지 못한다.
    (readObject 메서드가 실질적으로 또 다른 public 생성자이기 때문)
- 문제 해결 방법
  - readObject 메서드가 defaultReadObject를 호출한 다음 역질렬화된 객체가 유효한지 검사하자.
### 또 다른 문제점
- 정상 인스턴스에서 시작된 바이트 스트림 끝에 private Date의 필드로의 참조를 추가하면 가변 인스턴스를 만들어 낼 수 있다. 
- 공격자는 ObjectInputStream에서 인스턴스를 읽은 후 스트림 끝에 추가된 '악의적인 객체 참조'를 읽어 객체의 내부 정보를 얻을 수 있다.
```java
// 가변 공격의 예
public class MutablePeriod {
    //Period 인스턴스
    public final Period period;
    
    //시작 시각 필드 - 외부에서 접근할 수 없어야 한다.
    public final Date start;
    //종료 시각 필드 - 외부에서 접근할 수 없어야 한다.
    public final Date end;
    
    public MutablePeriod() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectArrayOutputStream out = new ObjectArrayOutputStream(bos);
            
            //유효한 Period 인스턴스를 직렬화한다.
            out.writeObject(new Period(new Date(), new Date()));
            
            /**
             * 악의적인 '이전 객체 참조', 즉 내부 Date 필드로의 참조 추가
             */
            byte[] ref = {0x71, 0, 0x7e, 0, 5}; // 참조 #5
            bos.write(ref); // 시작 start 필드 참조 추가
            ref[4] = 4; //참조 #4
            bos.write(ref); // 종료(end) 필드 참조 추가
            
            // Period 역직렬화 후 Date 참조를 훔친다.
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            period = (Period) in.readObject();
            start = (Date) in.readObject();
            end = (Date) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new AssertionError(e);
        }
    }
}

```
```java
// 공격 예시
public static void main(String[] args) {
    MutablePeriod mp = new MutablePeriod();
    Period p = mp.period;
    Date pEnd = mp.end;

    //시간을 되돌리자!
    pEnd.setYear(78);
    System.out.println(p);

    //60년대로 회귀!
    pEnd.setYear(60);
    System.out.println(p);
}
```
- 객체를 역직렬화 할 때는 클라이언트가 소유해서는 안되는 객체 참조를 갖는 필드를 모두 반드시 방어적으로 복사해야 한다.

### 방어적 복사, 유효성 검사를 수행하는 readObject 메서드
```java
private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    
    // 가변 요소들을 방어적으로 복사한다. (방어적 본사를 선행한다.)
    start = new Date(start.getTime());
    end = new Date(end.getTime());
    
    // 불변식을 만족하는지 검사한다.
    if(start.compareTo(end) > 0) {
       throw new InvalidObjectException(start + "가 " + end + "보다 늦다.");
    }
}
```
- final 필드는 방어적 복사가 불가능하여 아쉽지만 제거해야한다.


### 정리
- readObject 메서드를 작성할 때는 언제나 public 생성자를 작성하는 자세로 임해야 한다.
- readObject는 어떤 바이트 스트림이 넘어오더라도 유효한 인스턴스를 만들어내야한다.
- 바이트 스트림이 진짜 직렬화된 인스턴스라고 가정해서는 안 된다.