package pl.ciruk.security.reflection;

public class ExposeHiddenMethod {
    public static void main(String[] args) {
        B b = new B();
        Object result = null;
        try {
            result = b.getHiddenMethod().invoke(b);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        System.out.println("result = " + result);
    }
}
