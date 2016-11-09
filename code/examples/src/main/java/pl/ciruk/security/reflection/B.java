package pl.ciruk.security.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class B extends A {
    public MethodHandle getHiddenMethod() {
        try {
            MethodHandle handle = MethodHandles.lookup().findVirtual(getClass(), "getName", MethodType.methodType(String.class));
            return handle;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
