# 표준 예외를 사용하라

### 표준 예외를 사용하는 것의 장점
- API를 다른 사람이 익히고 사용하기 쉬워짐
- 나의 API를 사용한 프로그램도 낯선 예외를 사용하지 않게 되어 읽기 쉬움
- 예외 클래스 수가 적을 수록 메모리 사용량도 줄고 클래스를 적재하는 시간도 적음

### 대표적 표준 예외
- IllegalArgumentException
    - 호출자가 인수로 부적절한 값을 넘길 때 던지는 예외
    - null을 허용하지 않는 메서드에 null을 건네면 관례상 NullPointerException을 던짐
    - 어떤 시퀀스의 허용 범위를 넘는 값을 건네면 관례상 IndexOutOfBoundsException을 던짐
- IllegalStateException
    - 대상 객체의 상태가 호출된 메서드를 수행하기에 적합하지 않을 때 던짐
    - 제대로 초기화 되지 않은 객체를 사용하려 할 때 던짐
- ConcurrentModificationException
    - 단일 스레드에서 사용하려고 설계한 객체를 여러 스레드가 동시에 수정하려 할 때 던짐
    - 동시 수정을 확실히 검출할 수 있는 안정된 방법은 없으니 이 예외는 문제가 생길 가능성을 알려주는 정도의 역할
- UnsupportedOperationException
    - 클라이언트가 요청한 동작을 대상 객체가 지원하지 않을 때 던짐
- ArithmeticException, NumberFormatException
    - 복소수나 유리수를 다루는 객체를 작성할 경우

### 주의점
- Exception, RuntimeException, Throwable, Error는 직접 재사용하지 말라
    - 이 예외들은 다른 예외들의 상위 클래스이므로 안정적으로 테스트할 수 없다 추상클래스라고 생각하자
- API문서를 확인해서 해당 예외가 어떤 상황에서 던져지는지 확인 필요함
    - 예외의 이름뿐만 아니라 맥락도 부합해야 함
    - 더 많은 정보 제공이 필요하면 표준 예외를 확장해도 좋음
    - 단, 예외는 직렬화할 수 있고 직렬화에는 많은 부담이 따르므로(12장) 나만의 예외를 새로 만드는데 주의 필요함