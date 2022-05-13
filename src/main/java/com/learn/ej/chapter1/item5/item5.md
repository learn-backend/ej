# 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라

## 장점

- 클래스 유연성, 재사용성, 테스트 용이성이 좋아진다.

  - 클래스 유연성은 유연하게 변경이 가능하다?
  - 재사용성: 클라이언트 코드에 직접적으로 의존하지 않고, 내부 캡슐화 로직만 변경되면, 되므로 재사용성이 용이하다.
  - 테스트 용이성: 실제 객체가 아닌 Mock  객체로 테스트가 가능해진다. (ex: Random)
    - **객체를 내부에서 생성하지않고, 외부에서 생성한 후 주입 시켜주도록 작성해보자.**

  ```jsx
  public void getRandomValue() {
  	Random random = new Random();
  	return random.nextInt(100);
  }
  
  getRandomValue(new Random());
  
  public void getRandomValue(Random random) {
  	return random.nextInt(100);	
  }
  
  @Test
  void randomTest() {
  	getRandomValue(new MockRandom())
  }
  
  public class MockRandom extends Random {
  
          @Override
          public int nextInt(int bound) {
              return 1;
          }
      }
  ```

  - Spring의 DI, (모듈간의 결합도가 낮아지고 유연성이 높아질 수도 있다.)