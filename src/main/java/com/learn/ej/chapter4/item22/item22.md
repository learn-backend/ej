### 인터페이스는 타입을 정의하는 용도로만 사용하라
 - 인터페이스를 구현한다는것은 자신의 인스턴스로 무엇을 할수 있는지 클라이언트에 얘기해주는것이다.

#### 용도에 맞지 않는 사용법
- 메소드 없이 상수로만 이루어진 인터페이스
```java
public interface PhysicalConstants {
    // 아보가드로 수 (1/몰)
    double AVOCADOS_NUMBER = 6.022_140_857e23;

    // 볼츠만 상수 (J/K)
    double BOLTZMANN_CONSTANT = 1.380_648_52e-23;

    // 전자 질량(kg)
    double ELECTRON_MASS = 9.109_383_56e-31;
}
```
 - 클래스 내부에서 사용하는 상수는 외부 인터페이스가 아니라 내부 구현에 해당한다. 따라서 상수 인터페이스를 구현하는 것은 이 내부 구현을 클래스의 API로 노출하는 행위다.
 - 클래스가 어떤 상수 인터페이스를 사용하든 사용자에게는 아무런 의미가 없다. 반면, 이는 사용자에게 혼란을 주며 클라이언트 코드가 이 상수들에 종속되게 한다

#### 상수를 공개하는 목적으로 사용 가능한 방법
 - 특정 클래스나 인터페이스와 강하게 연관된 상수라면 그 클래스나 인터페이스 자체에 추가해야 한다
 - 열거 타입으로 나타내기 적합한 상수라면 열거 타입으로 만들어 공개하면 된다.
 - 인스턴스화할 수 없는 유틸리티 클래스에 담아 공개하는 방법도 있다.
 ```java
 public class PhysicalConstants {
    private PhysicalConstants() { }  // 인스턴스화 방지

    // 아보가드로 수 (1/몰)
    // 숫자 리터럴에 밑줄을 사용했다. 이 밑줄은 자바 7부터 허용되며 가독성을 향상시켜주니 사용해보자.
    public static final double AVOGADROS_NUMBER = 6.022_140_857e23;

    // 볼츠만 상수 (J/K)
    public static final double BOLTZMANN_CONST  = 1.380_648_52e-23;

    // 전자 질량 (kg)
    public static final double ELECTRON_MASS    = 9.109_383_56e-31;
}
 ```

#### 정적 임포트를 사용해 상수 이름만으로 사용하기
```java
import static PysicalConstants.*;    
public class Test {
    double atoms(double mols) {
        return AVOGADROS_NUMBER * mols;
    }
}
```

#### 정리
- 인터페이스는 타입을 정의하는 용도로만 사용해야하며 상수 공개용으로 사용하지 말자.