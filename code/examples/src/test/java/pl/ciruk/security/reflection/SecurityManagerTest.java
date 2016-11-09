package pl.ciruk.security.reflection;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class SecurityManagerTest {

    private static Field valueField;

    @BeforeClass
    public static void setUp() throws Exception {
        valueField = String.class.getDeclaredField("value");
        valueField.setAccessible(true);
    }

    @Test
    public void shouldModifyPrivateMember() throws Exception {
        String text = new String("Sample text");

        valueField.set(text, new char[]{'v', 'a', 'l', 'u', 'e'});

        assertThat(text, is(not(equalTo("Sample text"))));
    }
}
