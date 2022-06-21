# 타입 안전 이종 컨테이너를 고려하라
- 객체를 저장하는 저장공간(컨테이너)로서 Set, Map 등이 주로 사용이 된다
- 이들은 하나의 컨테이너에서 매개변수화할 수 있는 타입의 수가 제한이 된다
- 컨테이너대신 키를 매개변수화하고 값을 넣거나 뺄 때 키 타입을 제공해줌으로써 더 유연하게 사용할 수 있다
- 값의 타입이 키와 같음을 보장할 수 있게 된다
- 사용되는 경우
    - 타입의 수에 제약없이 유연하게 필요한 경우
    - 특정 타입 외에 다양한 타입을 지원해야하는 경우

### 타입 토큰
- 컴파일타임 타입 정보와 런타임 타입 정보를 알아내기 위해 메서드들이 주고받는 class 리터럴
- 타입 안전성이 필요한 곳에 쓰임

### 타입 안전 이종 컨테이너 패턴
```java
// 타입별로 인스턴스를 저장하고 검색할 수 있는 클래스
public class Favorite {
    private Map<Class<?>, Object> favorites = new HashMap<>();

    public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), type.cast(instance));
    }
    public <T> T getFavorite(Class<T> type) {
        return type.cast(favorites.get(type));
    }
}
```
- 각 타입의 Class 객체를 매개변수화한 키 역할로 사용
- class의 리터럴 타입은 Class가 아닌 `Class<T>`
    - String.class의 리터럴 타입은 `Class<String>`
- 맵이 아니라 키가 와일드카드 타입이다
    - 맵 안에 아무것도 넣을 수 없는 것이 아니라
    - 모든 키가 서로 다른 매개변수화 타입일 수 있다는 뜻
    - 와일드카드 타입이 중첩되었다고 표현
- favorites 맵의 값 타입은 단순히 Object
    - 키와 값 사이의 타입 관계를 보증하지 않으므로 모든 값이 키로 명시한 타입임을 보증하지 않음
    - putFavorite에서 키와 값 사이의 타입 관계는 버려지고 값이 키 타입의 인스턴스라는 정보가 없어진다
    - 이 관계는 getFavorite에서 되살린다
- 형변환
    - getFavorite에서 맵에서 꺼내진 값은 잘못된 컴파일타임 타입인 Object타입 가지고 있으므로 T로 바꾸는 작업이 필요
    - Class의 cast함수 이용
    - 맵의 값은 키타입과 항상 일치 함을 알고 있음


- 제약사항
    1. 클라이언트가 Class객체를 로타입으로 넘기면 타입 안전성이 쌔진다
        - 클라이언트 코드에서 컴파일할 때 비검사 경고가 뜰 것
        - Favorites가 타입 불변식을 어기는 일이 없도록 보장하려면 putFavorite 메서드와 같이 instance의 타입이 type으로 명시한 타입과 같은지 확인
    2. 실체화 불가 타입에는 사용할 수 없다
        - String이나 String[]은 저장할 수 있어도 즐겨 찾는 `List<String>`은 저장할 수 없다
        - `List<String>`과 `List<Integer>`는 List.class라는 객체를 공유하기 때문
        - 한정적 타입 토큰을 활용하면 가능
        ```java
        public <T extends Annotation> T getAnnotation(Class<T> annotationType);
        ```
        - Class<?> 타입의 객체를 한정적 타입 토큰을 받는 메서드에 넘기는 경우 
          Class<? extends Annotation>으로 형병환 하면 비검사 컴파일 경고가 뜨므로 asSubclass 메서드를 사용
        ```java
        static Annotation getAnnotation(AnnotatedElement element, String annotationTypeName) { 
            Class<?> annotationType = null; // 비한정적 타입 토큰
            try {
                annotationType = Class.forName(annotationTypeName);
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
            return element.getAnnotation(annotationType.asSubclass(Annotation.class));
        }
        ```