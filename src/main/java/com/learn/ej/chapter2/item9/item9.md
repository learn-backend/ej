### 아이템 9. try-finally보다는 try-with-resources를 사용하라

#### 자바 라이브러리에는 close 메서드를 직접 호출해줘야하는 자원이 많다.
- InputStream, OuputStream, java.sql.Connection

#### java7 이전에는 try-catch-finally 구문을 사용하여 자원을 해제함.
```java
public static void main(String args[]) throws IOException {
    FileInputStream is = null;
    BufferedInputStream bis = null;
    try {
        is = new FileInputStream("file.txt");
        bis = new BufferedInputStream(is);
        int data = -1;
        while((data = bis.read()) != -1){
            System.out.print((char) data);
        }
    } finally {
        // 객체에 대한 null 체크 필요
        // exception이 발생하면 다른 코드가 실행되지 않을수 있기에 close 코드를 넣어줘야함.
        if (is != null) is.close();
        if (bis != null) bis.close();
    }
}
```
- 
#### java7부터 Try-with-resources로 자원 해제 처리
```java
public static void main(String args[]) {
    // 복수의 자원을 처리하는 try-with-resources 
    try (
        FileInputStream is = new FileInputStream("file.txt");
        BufferedInputStream bis = new BufferedInputStream(is)
    ) {
        int data = -1;
        while ((data = bis.read()) != -1) {
            System.out.print((char) data);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

```
- try 문을 벗어나면 자동으로 close() 호출
- AutoCloseable 인터페이스를 구현한 객체만이 close()를 자동 호출
- 위에 예제는 AutoCloseable를 상속받은 Closeable이 구현되어 있음
```java
public abstract class InputStream extends Object implements Closeable {
}

public interface Closeable extends AutoCloseable {
    void close() throws IOException;
}
```