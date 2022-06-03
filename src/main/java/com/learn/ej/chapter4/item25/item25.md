# 톱 레벨 클래스는 한 파일에 하나만 담으라



소스파일 하나에 톱레벨 클래스를 여러개 선언하더라도 컴파일러는 예외를 던지지 않는다.

- IDE 에서는 에러를 보여주는 듯 하다. 단, 컴파일은 가능하다.

  ![CleanShot 2022-06-02 at 13 58 13@2x](https://user-images.githubusercontent.com/37217320/171556038-84e7511f-526b-4669-a6d5-299a9baeeb7e.png)



아래 에시는 Effective Java 3/E 에서 예시로 보여준 소스이다.

**Item25Main.java**

```java
public class Item25Main {

    public static void main(String[] args) {
        System.out.println(Utensil.NAME + Dessert.NAME);
    }

}

```



**Dessert.java**

```java
class Dessert {

    static final String NAME = "pot";

}

class Utensil {

    static final String NAME = "pie";
}

```



**Utensil.java**

```java
class Utensil {

    public static final String NAME = "pan";
}

class Dessert {

    public static final String NAME = "cake";

}

```





또한 컴파일 명령어에 순서에 따라 결과가 달라질 수 있다.

```bash
javac Dessert.java Item25Main.java
```

<img width="827" alt="CleanShot 2022-06-02 at 14 00 38@2x" src="https://user-images.githubusercontent.com/37217320/171556255-bce97613-e42d-4abf-beb8-9d5c5a9a7938.png">



```bash
javac Utensil.java Item25Main.java 
```

<img width="882" alt="CleanShot 2022-06-02 at 14 03 40@2x" src="https://user-images.githubusercontent.com/37217320/171556532-9af4c18a-27c0-463c-9b95-e0a4634aa3ab.png">





> 소스 파일 하나에는 반드시 톱레벨 클래스 (혹은 톱레벨 인터페이스)를 하나만 담자.