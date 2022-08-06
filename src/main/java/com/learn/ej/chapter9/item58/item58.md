# 전통적인 for문 보다는 for-each문을 사용하라

### 전통적인 for문
```java
// 컬렉션 순환하기
for (Iterator<Element> i = c.iterator() ; i.hasNext() ; ) {    
    Element e = i.next();    
    ... // e로 무언가를 한다.
}

// 배열 순환하기
for (int i = 0 ; i < a.length ; i++) {    
    ... // a[i]로 무언가를 한다.
}
```
- while문보다는 낫다
- 반복자, 인덱스 변수는 코드를 지저분하게 하고 필요가 없다
- 쓰이는 요소가 늘어나면 오류 가능성이 높아진다
- 잘못된 변수를 사용해도 컴파일러가 잡지 못한다
- 컬렉션이냐 배열이냐에 따라 코드 형태가 달라진다

### for-each문 (향상된 for문)
```java
for (Element e : elements) {
    ... // e로 무언가를 한다.
}
```
- 반복대상이 컬렉션이든 배열이든 속도는 for-each문을 사용해도 속도는 동일하다
- 중첩 순회할 때 이점이 커진다

### 중첩 순회 예시
```java
enum Suit { CLUB, DIAMOND, HEART, SPADE }
enum Rank { ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, 
            NINE, TEN, JACK, QUEEN, KING} 
...

static Collection<Suit> suits = Arrays.asList(Suit.values());
static Collection<Rank> ranks = Arrays.asList(Rank.values()); 

List<Card> deck = new ArrayList<>();
for (Iterator<Suit> i = suites.iterator(); i.hasNext(); )
    for (Iterator<Rank> j = ranks.iterator(); j.hasNext(); )
        deck.add(new Card(i.next(), j.next()));
```
- 마지막줄의 i.next()로 인해 Suit하나당이 아니라 Rank하나당 호출이 되고 있다
- 숫자가 바닥이 나면 NoSuchElementException이 발생할 수 있다
- 운이 나빠 바깥 컬렉션 크기가 안쪽 컬렉션 크기의 배수이면 예외도 던지지않고 종료 된다
- 버그를 발견하기 힘들다

```java
enum Face { ONE, TWO, THREE, FOUR, FIVE, SIX }
...
Collection<Face> faces = EnumSet.allOf(Face.class); 

for (Iterator<Face> i = faces.iterator(); i.hasNext(); )
    for (Iterator<Face> j = faces.iterator(); j.hasNext(); )
        System.out.println(i.next() + " " + j.next());
```
- 위와 동일한 버그인 경우이다
- 예외를 던지지는 않지만 잘못된 결과를 출력한다

```java
for (Iterator<Suit> i = suits.iterator(); i.hasNext(); ) {
    Suit suit = i.next();
    for (Iterator<Rank> j = ranks.iterator(); j.hasNext(); )
        deck.add(new Card(suit, j.next())));
}
```
- 첫 번째 예시의 버그를 고친 코드이다

```java
for (Suit suit : suits) 
    for (Rank rank : ranks) 
        deck.add(new Card(suit, rank));
```
- for-each문을 사용하면 코드가 훨씬 간결해진다

### for-each 문을 사용못하는 경우
- 파괴적인 필터링
    - 컬렉션을 순회하면서 선택된 원소를 제거해야 하는 경우
- 변형
    - 리스트나 배열을 순회하면서 원소의 값 일부 혹은 전체를 교체하는 경우
- 병렬 반복
    - 여러 컬렉션을 병렬로 순회하는 경우
    - 각각의 반복자와 인덱스 변수를 사용해 명시적으로 제어해야 한다
    
### 팁
- 원소들의 묶음을 표현하는 타입을 작성해야 한다면 Iterable을 구현하라
- Iterable을 구현하기만 하면 for-each문을 사용할 수 있다