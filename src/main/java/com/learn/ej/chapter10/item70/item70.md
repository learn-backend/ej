# 복구할 수 있는 상황에는 검사 예외를, 프로그래밍 오류에는 런타임 예외를 사용하라.

Java에서는 문제 상황을 알리는 타입으로 **검사 예외, 런타임 예외, 에러** 이렇게 세가지를 제공한다.



![CleanShot 2022-08-20 at 17 45 45@2x](https://user-images.githubusercontent.com/37217320/185736885-8bc98f51-73a9-4476-900b-e95c1a268a35.png)



## 검사 예외 (Checked Exception)

- 호출하는 쪽에서 복구하리라 여겨지는 상황인 경우 사용해라
- 검사 예외를 호출하면 반드시 예외처리를 컴파일 시점에 해야한다.
- 예외 상황에서 벗어나는 데 필요한 정보를 알려주는 메서드를 함께 제공해주는 것이 중요하다. (Item 75)



## 비 검사 예외 (UnChecked Exception)

- 비검사 예외는 크게 Runtime Exception과 Error 이다
- 프로그래밍 오류를 나타내며, 복구가 불가능 한 경우 사용해라



## ~~Throwable~~

- 비검사 throwable은 모두 RuntimeException의 하위 클래스여야 한다. (Error은 상속하지 말아라)
- Exception, RuntimeException, Error을 상속하지 않는 throwable을 만들수도 있지만 **절대로 만들어서 사용하지 말아라**
- throwable은 정상적인 검사 예외보다 나은점이 없으며, 사용자를 헷갈리게 만든다.



> 복구할 수 있는 예외라면 검사 예외를, 프로그래밍 오류라면 비검사 예외를 던지자.
>
> 검사 예외라면 복구에 필요한 정보를 알려주는 메소드도 제공하자.
>
> throwable은 정의하지도 말아라.