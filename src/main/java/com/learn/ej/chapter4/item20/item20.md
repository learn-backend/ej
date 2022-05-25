#추상 클래스보다는 인터페이스를 우선하라

###디폴트 메서드
- 중복메서드의 구현을 없애주었다
- @implSpec 으로 문서화해야 한다
- Object의 equals, hashcode 같은 메소드는 디폴트 메소드로 제공해서는 안 된다
- public이 아닌 정적 멤버도 가질 수 없다
- 본인이 만들지 않은 인터페이스에는 디폴트 메소드를 추가할 수 없다

###인터페이스를 구현한 클래스는 어떤 클래스를 상속하든 같은 타입
- 자바는 단일 상속만 허용, 추상 클래스 방식은 새로운 타입 정의에 제약이 생김
   
###기존 클래스에도 손쉽게 새로운 인터페이스를 구현해 넣을 수 있다
- 추상클래스는 계층구조의 혼란 발생
- 두 클래스가 같은 추상클래스를 상속받으면 계층적으로 공통의 조상으로 묶이게 되는데 두 클래스가 연관이 아예 없는 경우

###믹스인 정의에 안성맞춤이다
- 믹스인 : 클래스의 주된 기능외에 선택적 기능을 혼합하는 것, 주 기능외에 추가적인 기능을 제공해주는 효과가 있음
- 클래스는 두 부모를 섬길 수 없고 클래스 계층구조에 믹스인을 삽입하기 합리적 위치가 없기 때문에 추상클래스로는 정의 불가능
- 예시) Comparable, Cloneable, Serializable
    
###계층구조가 없는 타입 프레임워크를 만들 수 있다
- 클래스가 여러 인터페이스 구현 가능
```java
public class People implements Singer, SongWriter {
    @Override
    public void Sing(String s) {

    }
    @Override
    public void Compose(int chartPosition) {

    }
}
```
- 여러 인터페이스를 확장하고 새로운 메서드도 추가가능
```java
public interface SingerSongWriter extends Singer, SongWriter {
    void strum();
    void actSensitive();
}
```
- 추상클래스를 상속받는 방법은 모든 조합에 대한 추상클래스를 만들어야 한다 이것을 조합 폭발(combinatorial explosion)이라고 한다.
```java
public abstract class Singer {
    abstract void sing(String s);
}

public abstract class SongWriter {
    abstract void compose(int chartPosition);
}

public abstract class SingerSongWriter {
    abstract void strum();
    abstract void actSensitive();
    abstract void Compose(int chartPosition);
    abstract void sing(String s);
}
```

###래퍼 클래스 관용구와 함께 사용하면 기능을 향상시키는 안전하고 강력한 수단이 된다
- 사용경험이 있으신가요??

###템플릿 메서드 패턴
- 인터페이스로 타입 정의, 디폴트 메서드 정의
- 골격 구현 클래스는 나머지 메서드를 구현, 관례상 'Abstract+인터페이스이름'으로 네이밍

```java
// 인터페이스
public interface Vending {
    void start();
    void chooseProduct();
    void stop();
    void process();
}

// 추상골격 구현 클래스
public abstract class AbstractVending implements Vending {
    @Override
    public void start() {
        System.out.println("vending start");
    }

    @Override
    public void stop() {
        System.out.println("stop vending");
    }

    @Override
    public void process() {
        start();
        chooseProduct();
        stop();
    }
}

// 추상골격 구현으로 중복이 제거됨
public class BaverageVending extends AbstractVending implements Vending {
    @Override
    public void chooseProduct() {
        System.out.println("choose menu");
        System.out.println("coke");
        System.out.println("energy drink");
    }
}

public class CoffeeVending extends AbstractVending implements Vending {
    @Override
    public void chooseProduct() {
        System.out.println("choose menu");
        System.out.println("americano");
        System.out.println("cafe latte");
    }
}
```

####시뮬레이트한 다중 상속
- 위와 같이 인터페이스를 구현한 클래스에서 해당 골격 구현을 확장한 private 내부 클래스를 정의하고 
  각 메소드 호출을 내부 클래스의 인스턴스에 전달하여 골격 구현 클래스를 우회적으로 이용하는 방식

```java
// Vending을 구현하는 구현 클래스가 VendingManuFacturer 라는 제조사 클래스를 상속받아야해서 
// 추상 골격 구현을 확장하지 못하는 상황
public class VendingManufacturer {
    public void printManufacturerName() {
        System.out.println("Made By JavaBom");
    }
}

public class SnackVending extends VendingManufacturer implements Vending {
    InnerAbstractVending innerAbstractVending = new InnerAbstractVending();

    @Override
    public void start() {
        innerAbstractVending.start();
    }

    @Override
    public void chooseProduct() {
        innerAbstractVending.chooseProduct();
    }

    @Override
    public void stop() {
        innerAbstractVending.stop();
    }

    @Override
    public void process() {
        printManufacturerName();
        innerAbstractVending.process();
    }

    private class InnerAbstractVending extends AbstractVending {

        @Override
        public void chooseProduct() {
            System.out.println("choose product");
            System.out.println("chocolate");
            System.out.println("cracker");
        }
    }
}
```

####단순 구현
- 골격 구현의 작은 변종으로 골격 구현과 같이 상속을 위해 인터페이스를 구현한 것이지만, 
  추상 클래스가 아니라는 점에서 차이점
- ex) AbstractMap.SimpleEntry