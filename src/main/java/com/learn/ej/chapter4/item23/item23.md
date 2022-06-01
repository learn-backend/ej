### 태그 달린 클래스보다는 클래스 계층구조를 활용하라
 - 태그란 해당 클래스가 어떠한 타입인지에 대한 정보를 담고 있는 멤버 변수를 의미
#### 태그 달린 클래스 문제점
- 비효율적이며 불필요한 코드가 많아짐
  - 열거 타입 선언, 태그필드, switch 등 불필요한 코드 
- 가독성이 나빠지고 불필요한 메모리를 사용하게됨

```java
public class Figure {
    enum Shape {RECTANGLE, CIRCLE}

    //태그 필드 - 현재 모양을 나타낸다.
    private Shape shape;

    // 다음 필드들은 모양이 사각형일 때만 사용.
    private double length;
    private double widthㅁ;

    // 다음 필드들은 모양이 원일 때만 싸용.
    private double radius;

    //원용 생성자
    public Figure(double radius) {
        shape = Shape.CIRCLE;
        this.radius = radius;
    }

    //사각형용 생성자
    public Figure(double length, double width) {
        shape = Shape.RECTANGLE;
        this.length = length;
        this.width = width;
    }

    private double area() {
        switch (shape) {
            case RECTANGLE:
                return length + width;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError(shape);
        }
    }
}
```
- circle, rectangle 의 2가지 책임을 가져 단일 책임 원칙에 어긋난다.
- 다른 도형을 추가한다고 가정하면 기존의 코드에 열거타입 선언, 태그필드 switch등 추가해야될 부분이 많다.
  - 기존의 코드를 수정하지 않고도 앱의 확장이 가능해야한다.(개방 - 패쇄 원칙)

#### 클래스 계층 구조
```java
abstract class Figure {
  abstract dobule area(); 
}

class Circle extends Figure {
  final double radius;
  
  Circle(double radius) {this.radius = radius;}
  
  @Override double area() {return Math.PI * (radius * radius);}
}

class Rectangle extends Figure {
  final double length;
  final double width;
  
  Rectangle(double length, double width) {
    this.length = length;
    this.width = width;
  }
  
  @Override double area() {return length * width;}
}
```
- 공통 부분 추출하여 상위클래스로 올리고 변경부분만 하위클래스로 생성

#### 타입 추가시 아래 클래스만 추가
```java
class Square extends Rectangle { 
	Square(double side) {
    super(side,side);
  }
}
```
#### 정리
- 태그 달린 클래스를 써야 하는 상황은 거의 없다. 
- 새로운 클래스를 작성하는 데 태그 필드가 등장한다면 태그를 없애고 계층구조로 대체하는 방법을 생각해보자. 
- 기존 클래스가 태그 필드를 사용하고 있다면 계층구조로 리팩토링 하는걸 고민해보자.