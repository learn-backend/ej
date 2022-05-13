# 1장 정적 팩토리 메소드

## 장점

- 생성자의 시그니처가 중복되는 경우에 고려해볼 수 있다.

- 인스턴스 생성을 통제할 수 있다.

- 객체가 자주 요청 & 생성되는 경우 성능을 올릴 수 있다.

  - ex) flyweight Pattern
    - 자주 사용하는 값들을 미리 캐싱해서 넣어두고 꺼내는 방식
    - 공유 객체에 의해 메모리에 로드 되는 객체의 개수를 줄일 수 있다.

- 인터페이스 기반의 리턴이 가능하다.

  - 구체적인 타입을 클라이언트에게 숨길 수 있다.
  - Java 8 이후부터는 static 으로 생성 로직을 인터페이스에 넘길 수 있음.

  ```jsx
  public interface Pizza {
  	
  	Pizza of(PizzaType pizzaType) { // 정적 팩토리 역할을 대신해줌 (ex: List.of(), Map.of()..)
  		switch (pizzaType) {
  			CHEESE: {
  				return new CheesePizza();
  			}
  			SHRIMP: {
  				return new ShrimpPizza();
  			}
  			return DefaultPizza();
  		}
  	}
  }
  ```

## 단점

- 상속을 받을 수 없다. (생성자를 Private로 감싸기 때문에,)
- 단, 위임패턴으로 넘어가야하기 때문에 장점일 수도 있다. (결합도를 낮추고 응집도를 올릴 수 있다, 유지보수를 용이하게 해준다.)

```jsx
public class Computer implements KeyBoard { // 상속
	
	@Override	
	public Event onPressDown() {
		// todo...
	}
	
	@Override	
	public Event onPressUp() {
		// todo...
	}
}

public class Computer { // 위임
	public KeyBoard keyBoard;
	
	public Computer(KeyBoard keyboard) {
		this.keyboard = keyboard;	
	}

	public Event onPressDown() {
		return this.keyBoard.onPressDown();
	}
	
	public Event onPressUp() {
		return this.keyBoard.onPressUp();
	}
	
}
```

값을 넘겨 받아서 생성시 (of, 새로 생성시 create, 별도 클래스를 가지고 있는경우,

생성자를 사용하되, 모든 경우에 적절하지는 않다.