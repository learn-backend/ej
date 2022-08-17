### 리플렉션보다는 인터페이스를 사용하라
리플랙션을 이용하면 임의의 클래스에 접근할수 있다.
- 클래스의 생성자, 메서드, 필드에 해당하는 constructor, method, field 인스턴스를 가져올 수 있다.
- 클래스의 멤버 이름, 필드 타입, 메서드 시그니처 등을 가져올 수 있다.
- 컴파일 당시에 존재하지 않던 클래스도 이용할 수 있다.

### 리플렉션 단점
- 컴파일타임 타입 검사가 주는 이점을 하나도 누릴 수 없다.
- 리플렉션을 이용하면 코드가 지저분해지고 장황해진다.
- 성능이 떨어진다.

### 리플렉션은 아주 제한된 형태로만 사용하자.
- 인터페이스 및 상위클래스로 참조해 사용하자. 
- 즉, 리플렉션은 인스턴스 생성에만 쓰고, 이렇게 만든 인스턴스는 인터페이스나 상위클래스로 참조해 사용하자.

```java
// Set<String> 인터페이스를 사용한 예제
// args를 통해 첫 번째 인수로 지정한 클래스가 무엇이냐에 따라 저장되는 순서(구조)가 달라진다.(런타임)
// HashSet을 지정하면 무작위 순서가 될 것이고, TreeSet을 지정하면 알파벳 순서가 될 것이다
public static void main(String[] args) {
        Class<? extends Set<String>> cl = null;
        // 클래스 이름을 클래스 객체로 변환
        try {
        cl = (Class<? extends Set<String>>) Class.forName(args[0]);
        } catch (ClassNotFoundException e) {
        fatalError("클래스를 찾을 수 없습니다.");
        }

        // 생성자를 얻어오기
        Constructor<? extends Set<String>> cons = null;
        try{
        cons = cl.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
        fatalError("매개변수 없는 생성자를 찾을 수 없습니다.");
        }

        // 위에서 만든 클래스를 통해 집합 인스턴스 생성
        Set<String> s = null;
        try{
        s = cons.newInstance();
        } catch (IllegalAccessException e) {
        fatalError("생성자에 접근할 수 없습니다.");
        } catch (InstantiationException e) {
        fatalError("클래스를 인스터화 할 수 없습니다.");
        } catch (InvocationTargetException e) {
        fatalError("생성자가 예외를 던졌습니다." + e.getCause());
        } catch (ClassCastException e){
        fatalError("Set을 구현하지 않은 클래스 입니다.");
        }

        // 생성한 인스턴스 사용
        s.addAll(Arrays.asList(args).subList(1, args.length));
        System.out.println(s);
        }

private static void fatalError(String msg){
        System.out.println(msg);
        System.exit(1);
        }
```
 - 런타임 중에 총 여섯가지나 되는 예외를 던질 수 있다
 - 클래스 이름만으로 인스턴스를 생성해내기 위해 무려 25줄 이상의 코드를 작성했다.


#### 정리
 - 리플렉션은 복잡한 시스템을 개발할때 강력한 기능이지만 단점도 많다.
 - 컴파일타임에는 알수없는 클래스를 사용하는 프로그램을 개발한다면 리플렉션을 사용해야한다.
 - 단 되도록 객체 생성에만 사용하고, 생성한 객체를 이용할때는 적절한 인터페이스나 
 - 컴파일 타임에 알수 있는 상위 클래스로 형변환해 사용해야한다.