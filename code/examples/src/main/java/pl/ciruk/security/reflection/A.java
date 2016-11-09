package pl.ciruk.security.reflection;

import java.util.UUID;

public class A implements Cloneable{
    @Override
    protected A clone() throws CloneNotSupportedException {
        return ((A) super.clone());
    }

    private String name = UUID.randomUUID().toString();

    protected String getName() {
        return name;
    }
}
