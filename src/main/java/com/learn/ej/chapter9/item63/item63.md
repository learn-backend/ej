### 문자열 연결은 느리니 주의하라
- 문자열 연결 연산자(+)는 여러 문자열을 하나로 합쳐주는 편리한 수단이다.
- 문자열 연결 연산자로 문자열 n개를 잇는 시간은 n^2에 비래한다.
- 문자열은 불변객체라 연결하기위해서는 양쪽의 내용을 복사해 새로운 문자열을 만들기 때문이다.

### StringBuilder
- 성능을 포기하고 싶지 않다면 String 대신 StringBuilder를 사용하자.
- StringBuffer에 비해 Thread-Safe 하지 않지만, 싱글쓰레드 환경에서는 연산처리가 굉장히 빠르다.

### StringBuffer
- StringBuilder와 기능은 같고 멀티쓰레드 환경을 고려해 synchronized 키워드를 사용해 동기화를 할 수 있다.

#### 정리
- 많은 문자열을 연결할때는 문자열 연결 연산자(+)가 아닌 StringBuilder, StringBuffer를 사용하자.