#상속보다는 컴포지션을 사용하라

###상속의 단점
 메서드 호출과 달리 상속은 캡슐화를 깨뜨린다
 - 컴포지션을 써야 할 상황에서 상속을 사용하는 건 내부 구현을 노출하는 것이다
 - API가 내부구현에 묶이게 됨
 - 상위 클래스의 변동으로 하위 클래스가 영향을 받는다

1. 상위 클래스에서 메서드를 재정의
```java
public class InstrumentedHashSet<E> extends HashSet<E> {
    private int addCount = 0;

    public InstrumentedHashSet(){}
    
    public InstrumentedHashSet(int initCap, float loadFactor){
        super(initCap, loadFactor);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}
```
2. 상위 클래스에서 새로운 메서드 추가
   - 상위 클래스의 메서드 추가로 하위 클래스의 규칙이 깨지는 경우
   - 하위 클래스에서 정의한 메서드가 상위 클래스 다음 릴리즈에 같은 이름으로 추가되는 경우
```java
public class Animal {
    private int moveCount = 0;
    private long cordX = 0;
    private long cordY = 0;

    public void moveLeft(int distance) {
        this.cordX -= distance;
        moveCount++;
    }

    public void moveRight(int distance) {
        this.cordX += distance;
        moveCount++;
    }

    public void moveDown(int distance) {
        this.cordY -= distance;
        moveCount++;
    }

    public void moveUp(int distance) {
        this.cordX += distance;
        moveCount++;
    }
}
```
```java
// 움직이지 않는 Animal에 대한 class
public class StopAnimal extends Animal {
    @Override
    public void moveLeft(int distance) {
        System.out.println("can't move");
    }
    
    @Override
    public void moveRight(int distance) {
        System.out.println("can't move");
    }

    @Override
    public void moveDown(int distance) {
        System.out.println("can't move");
    }

    @Override
    public void moveUp(int distance) {
        System.out.println("can't move");
    }

    public void jump() {
        System.out.println("jump");
    }
}
``` 

```java
public class Animal {
  // ...
  // ...
  // ...

  // 상위 클래스의 메서드 추가로 하위 클래스의 규칙이 깨지는 경우
  public void moveDoubleLeft(int distance) {
      this.cordX -= distance * 2;
      moveCount++;
  }

  // 하위 클래스에서 정의한 메서드가 상위 클래스 다음 릴리즈에 같은 이름으로 추가되는 경우
  public boolean jump() {
      if (this.cordX > 0) {
          System.out.println("jump");
          return true;          
      }
      else {
          return false;          
      }
  }
}
```
        
###컴포지션
- private 필드로 기존 클래스의 인스턴스를 참조하는 것
- forwarding : 새 클래스의 인스턴스 메서드를 기존 클래스에 대응하여 호출
- forwarding method : 새 클래스의 메서드
- wrapper class : 바깥 클래스
- 데코레이터 패턴
- 넓은 의미로 위임

```java
public class ForwardingAnimal {
    private final Animal animal;
    
    @Override
    public void moveLeft(int distance) {
        this.animal.moveLeft(distance);
    }
    
    @Override
    public void moveRight(int distance) {
        this.animal.moveRight(distance);
    }

    @Override
    public void moveDown(int distance) {
        this.animal.moveDown(distance);
    }

    @Override
    public void moveUp(int distance) {
        this.animal.moveUp(distance);
    }

    public void jump() {
        System.out.println("jump");
    }
}
``` 

###래퍼클래스의 단점
- 콜백 프레임워크와 어울리지 않는다
- 자신의 참조를 다른 객체에 넘겨서 다음 호출(콜백)때 사용하도록 한 콜백 프래임워크에서 내부 객체는 자신을 감싸고 있는 래퍼의 존재를 모르니 
  자신의 참조를 넘기고, 콜백 때는 래퍼가 아닌 내부 객체를 호출하게 되기 때문

###상속을 사용할 경우 확인해야 할 것
- is-a 관계인지
- 상위 클래스가 확장을 고려해 설계가 되었는지
- 상위 클래스가 문서화가 잘 되었는지