# public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라



아래 public 클래스를 작성할때 아래와 같이 작성하지 말자.

**public 필드 사용 (비권장)**

```java
public class Point {
	public double x; 
	public double y;
}
```



**접근자와 변경자 메서드를 활용한 사용 (권장)**

```java
public class Point {
	private double x; 
	private double y;
  
  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  public double getX() { return x; }
  public double getY() { return y; }
  
  public void setX(double x) { this.x = x; }
  public void setY(double y) { this.y = y; }
}
```



**장점**

- 내부 표현 방식을 언제든 바꿀 수 있는 유연성을 얻을 수 있다.



다만, `package-private` 클래스 혹은 `private` 중첩 클래스라면 데이터필드를 노출한다 해도 하등의 문제가 없다.

> 클라이언트는 해당 클래스를 포함하는 패키지 안에서만 존재 하므로, 패키지 내부에서만 변경이 가능하다.



**사례**

- java.awt.package 패키지내 `Point`와 `Dimension` 클래스가 내부를 노출하였기에 오늘날까지도 해결되지 못했다고 한다.





**의견**

- 일반적으로 사용하는 Lombok은 어노테이션을 선언하여 `getter / setter`을 생성할 수 있다. 다만, 필드명이 변경되면 `getter / setter` 의 메소드명 역시 변경되므로 클라이언트에 영향을 주게 된다. 

- 가능하면 getter, setter을 지양하고, 객체에 메시지를 보내는 방식으로 구현한다면, 위의 단점을 해소할 수 있지 않을까 싶다.

  [오브젝트 (1) - 좋은 설계란? / 데이터 주도 설계 vs 책임 주도 설계](https://eunjin3786.tistory.com/324)



