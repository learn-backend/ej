### @Override 애너테이션을 일관되게 사용하라
 - @Override 애너테이션을 일관되게 사용하면 여러 가지 악명 높은 버그들을 예방해준다.

#### 버그 예제
```java
 // 영어 알파벳 2개로 구성된 문자열(바이그램)을 표현하는 클래스  
public class Bigram {
    private final char first;
    private final char second;

    public Bigram(char first, char second) {
        this.first  = first;
        this.second = second;
    }
    
    // 버그 유발 
    public boolean equals(Bigram b) {
        return b.first == first && b.second == second;
    }
    
    // 버그를 고친 부분
//    @Override public boolean equals(Object o) {
//        if (!(o instanceof Bigram2))
//            return false;
//        Bigram2 b = (Bigram2) o;
//        return b.first == first && b.second == second;
//    }

    public int hashCode() {
        return 31 * first + second;
    }

    public static void main(String[] args) {
        Set<Bigram> s = new HashSet<>();
        for (int i = 0; i < 10; i++)
            for (char ch = 'a'; ch <= 'z'; ch++)
                s.add(new Bigram(ch, ch));
        System.out.println(s.size());
    }
}
```
 - 위에 예제는 equals를 '재정의(overriding)'한 게 아니라 '다중정의(overloading)'해버렸다
 - Object의 equals를 재정의하려면 매개변수 타입을 Object로 해야만 하는데, 그렇게 하지 않은 것이다.

#### 정리
 - 상위 클래스의 메서드를 재정의하려는 모든 메서드에 @Override 애너테이션을 달자.
 - 구체 클래스에서 상위 클래스의 추상 메서드를 재정의한 경우엔 이 애너테이션을 달지 않아도 되지만 단다고해서 문제되지 않는다.
   - 상위 클래스의 메서드를 재정의시에는 다는 습관을 하자.