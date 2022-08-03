# 지역변수의 범위를 최소화하라

지역변수의 유효 범위를 최소로 줄이면 코드 가독성과 유지보수성이 높아지고 오류 가능성은 낮아진다. 지역변수의 범위를 줄이는 방법은 아래와 같은 방법이 있다.



### 가장 처음 쓰일 때 선언하기

지역변수를 미리 선언해두지 말고 필요할때 선언해주자.

### 선언과 동시에 초기화 하기

초기화에 필요한 정보가 충분하지 않다면 선언을 미뤄두자. 다만 `try-catch` 블록으로 감싸야 하고 try 블록 바깥에서도 사용해야 하는 경우는 예외이며, 한눈에 들어오게 try 블록 바로 앞에서 선언해주자.

### 반복문의 변수값을 반복문에서만 사용한다면 while 보다는 for문을 고려하기

반복문이 종료된 뒤에도 써야하는 상황이 아니라면 while문보다 for문을 쓰는 편이 낫다. (지역변수의 스코프를 한정할 수 있다.)

```java
Iterator<Element> i = c.iterator();
while (i.hasNext()) {
	doSomething(i.next());
}

...

Iterator<Element> i2 = c2.iterator();
while (i.hasNext()) { // 복붙이 잘못된 경우에도 정상 동작 한다.
	doSomething(i2.next());
}

```



```java
for (Iterator<Element> i = c.iterator(); i.hasNext();) {
	Element e = i.next();
	...
}

// 복붙이 잘못(?)된 경우 컴파일 에러가 발생하며 변수명을 동일한 이름의 변수명으로 해도 된다.
for (Iterator<Element> i2 = c2.iterator(); i.hasNext();) { 
	Element e2 = i2.next();
}
```



### 메서드를 작게 유지하고 한 가지 기능에 집중하도록 작성하기

한 메서드에서 여러가지 기능을 처리한다면 여러 지역변수가 섞일 수 있다.

가장 좋은 방법은 단순히 **메서드를 한가지 기능만 담당하도록 기능별로 분리**하자.