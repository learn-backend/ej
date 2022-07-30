# null이 아닌, 빈 컬렉션이나 배열을 반환하라



null을 반환하는 경우에는 **아래와 같이 null을 처리하는 코드**를 추가로 작성해야 한다.

```java
List<User> users = userService.getUsers(); // null을 반환할 수 있음
if (users != null && users.contains(UserType.ADMIN)) {
	System.out.println("admin users = " + users);
}
```



다만 빈 컨테이너를 할당하는 데도 비용이 든다는 주장도 있지만 이는 아래와 같인 이유로 틀린 주장이다.

- 성능 분석 결과 이 할당이 성능 저하의 주범이라고 확인되지 않는 한 이정도의 성능차이는 미미하다.
- 빈 컬렉션과 배열은 굳이 새로 할당하지 않아도 반환할 수 있다. (`Collections.emptyList()`)

Set이나 Map도 아래와 같이 빈 값을 지원한다.

- `Collections.emptySet()`
- `Collections.emptyMap()`

> 책에서는 최적화에 필요하니 꼭 필요할때만 사용하자고 나와있다. 다만, 개인적으로는 필요시 빈 값을 반환하는 케이스는 위 값을 사용해도 이슈가 없다는 생각을 가지고 있다.

```java
public List<Users> getUsers() {
	return users.isEmpty() ? Collections.emptyList() : new ArrayList<>(users);
} // users를 바로 반환해도 될것 같지만, 예시를 위해 위와 같이 표현한 것 같다.
```



배열 역시도 null을 반환하지 말고 길이가 0인 배열을 반환해라.

```java
public User[] getUsers() {
	return users.toArray(new User[0]);
}
```



성능을 떨어트릴 것 같으면 0짜리 배열을 미리 선언해두고 매번 그 배열을 반환해도 된다.

```java
private static final User[] EMPTY_USER_ARRAY = new User[0];

public User[] getUsers() {
	returnr users.toArray(EMPTY_USER_ARRAY);
}
```



 다만, 성능을 개선할 목적이라면 toArray에 넘기는 배열을 미리 할당하는 것을 권장하지는 않는다고 한다. 오히려 성능이 떨어진다는 연구결과도 있다. 아래와 같은 예시는 성능이 나빠지므로 지양하자.

```java
return users.toArray(new User[users.size()])
```

> <T> T[] List.toArray(T[] a) 메서드는 주어진 배열 a가 충분히 크면 a안에 원소를 담아 반환하고 그렇지 않으면 T[] 타입 배열을 새로 만들어 그 안에 원소를 반환한다.



