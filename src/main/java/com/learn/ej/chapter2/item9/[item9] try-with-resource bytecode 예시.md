

# Try-with-resource bytecode 예시



Item9장의 내용인 Try-with Resource를 사용했을때, bytecode가 어떻게 나오는지 확인해보았다.

**테스트 환경: adoptopenjdk-11.jdk**



Effective Java에 나온 설명과 같이 close() 메소드를 내부적으로 호출하고 있으며,

 close() 메소드를 호출하는 시점에 예외가 발생할 것을 고려해, try-catch로 한번 더 감싸주는것을 볼 수 있다.

> 개발자가 저부분을 다 고려할 필요가 없다는 점이 매우 큰 장점이지 않을까 생각된다.

### .java File

```java
public class TryWithResourceMain {

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

}
```





### .class File

```java
public class TryWithResourceMain {
    public TryWithResourceMain() {
    }

    public static void main(String[] args) {
        try {
            FileInputStream is = new FileInputStream("file.txt");

            try {
                BufferedInputStream bis = new BufferedInputStream(is);

                try {
                    boolean var3 = true;

                    int data;
                    while((data = bis.read()) != -1) {
                        System.out.print((char)data);
                    }
                } catch (Throwable var7) {
                    try {
                        bis.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }

                    throw var7;
                }

                bis.close();
            } catch (Throwable var8) {
                try {
                    is.close();
                } catch (Throwable var5) {
                    var8.addSuppressed(var5);
                }

                throw var8;
            }

            is.close();
        } catch (IOException var9) {
            var9.printStackTrace();
        }

    }
}

```

