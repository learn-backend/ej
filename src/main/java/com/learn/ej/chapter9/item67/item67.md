# 최적화는 신중히 하라



최적화를 섣불리 진행하면 빠르지도 않고 제대로 동작하지도 않으면서 수정하기는 어려운 소프트웨어를 탄생시킨다.

- 성능 때문에 견고한 구조를 희생시키지 말아라.
- **빠른 프로그램보다는 좋은 프로그램을 작성하라.**
  - 좋은 프로그램은 캡슐화를 통해 내부가 독립적이므로, 타 시스템에 영향을 주지 않고도 각 요소를 다시 설계할 수 있다.



## 좋은 설계 방법

- 설계 단계에서 반드시 성능을 미리 염두해두고 설계하여라
- 성능을 제한하는 설계를 피하라
- API를 설계할 때 성능에 주는 영향을 고려하라
  - public 타입으로 내부 데이터를 변경할 수 있게 만들면 **불필요한 방어적 복사를 수없이 유발할 수 있다.**
  - 컴포지션으로 해결할 수 있는 내용을 상속방식으로 설계하면 상위 클래스에 영원히 종속되며 그 성능 제약까지도 물려받게 된다.
  - 인터페이스도 있는데 굳이 구현 타입을 사용하지 말자. 나중에 더 빠른 구현체가 나오더라도 이용하지 못하게 된다.



## 최적화 주의사항

- ~~하지마라, (전문가 한정) 아직 하지마라.~~
- 성능을 위해 API를 왜곡하는것은 매우 안좋은 생각이다. 잘 설계된 API는 성능도 좋은게 보통이다.
- 최적화 전후로 반드시 프로파일링 도구를 이용해 성능을 측정하라.
- 여러가지 자바 플랫폼이나 여러 하드웨어 플랫폼에서 구동한다면 최적화 효과를 각각 측정해라.



> 좋은 프로그램을 작성하다 보면 성능은 따라온다.
>
> 다만 시스템을 설계할때 특히 API, 네트워크 프로토콜, 영구 저장용 데이터 포멧을 설계할때는 성능을 고려하라 (변경이 어렵다.)
>
> 성능이 안나온다면, 프로파일러를 사용해 원인이 되는 지점을 찾아 최적화를 수행해라.
>
> 모든 변경 후에는 성능을 측정하라.