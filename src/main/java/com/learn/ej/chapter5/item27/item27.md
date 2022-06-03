# 비검사 경고를 제거하라



할수 있는 한 모든 비검사 경고를 제거하라.

- 비검사 형변환 경고
- 비검사 메서드 호출 경고
- 비검사 매개변수화 가변인수 타입 경고
- 비검사 변환 경고
- 등..

예시로 아래와 같은 비검사 경고는 개선이 가능하다.

<img width="893" alt="CleanShot 2022-06-02 at 15 25 24@2x" src="https://user-images.githubusercontent.com/37217320/171566575-a8582325-fec2-40b8-a625-4e361d653413.png">

```java
Set<Integer> set = new HashSet<>();
```





경고를 제거할 수는 없지만, 타입 안전하다고 확신할 수 있다면 `@SuppressWarnings("unchecked")` 어노테이션을 달아 경고를 숨겨라.

- 어노테이션의 scope는 가능한 한 좁은 범위에 적용하자

  > 넓은 범위에 적용해버리면 심각한 경고를 놓칠 수도 있다.





**가정**

- 파라미터로 오는 타입을 List에서 하위 호환성을 유지하기 위해 변경할 수 없다고 가정
- 해당 메소드를 호출하는 부분은 String type이 보장된다고 하는 경우 아래와 같이 사용할 수 있다.

```java
   public List<String> listTypeConvert(List legacyList) {
        @SuppressWarnings("unchecked") 
        // 해당 메소드는 String Type의 List만 들어와야 한다.
        List<String> legacyList1 = legacyList;
        return legacyList1;
    }
```

단 좋은 예제는 아니며, 가능한 type을 보장할 수 있도록 제네릭으로 개선 하는 편이 좋아보인다.





> 비검사 경고는 중요하니 무시하지 말아라, 단 코드가 안전함이 증명된다면 가능한 범위를 좁혀 @SuppressWarnings("unchecked") 어노테이션과 주석을 남겨라.



 