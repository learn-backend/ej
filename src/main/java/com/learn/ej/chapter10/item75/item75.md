### 예외의 상세 메시지에 실패 관련 정보를 담으라
 - 예외 정보가 프로그램 실패에 대한 유일 정보인 경우가 많으며 재현하기 어려운 케이스일경우
 - 상세 정보가 없으면 문제 해결이 어려워질수 있다.

### 주의 사항
- 예외에 관여된 모든 매개변수와 필드 정보를 포함시키자.
- 예외 메시지는 장황해서는 안되며 문제 해결에 필요한 내용들만 담자.
- 예외는 실패와 관련된 정보를 얻을 수 있는 접근자 메서드를 적절하게 제공하는 것도 하나의 방법이다.
- 예외의 상세 메시지와 사용자가 받는 오류메시지와는 혼동하면 안된다.
  - 사용자가 받는 에러 메시지는 사용자 친화적인 문구로 처리해야한다.

#### 정리
- 예외 상세 메시지는 실패 원인 분석에 필요한 상세 정보들만 담자.
- 예외의 상세 메시지는 문제를 해결하는데 유용하다.