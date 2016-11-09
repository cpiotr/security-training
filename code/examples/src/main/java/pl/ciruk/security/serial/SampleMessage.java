package pl.ciruk.security.serial;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class SampleMessage implements Serializable {
    String title;
    String content;

    public SampleMessage(String title, String content) {
        this.title = title;
        this.content = content;
    }

    private void writeObject(ObjectOutputStream outputStream)
            throws IOException {
        outputStream.defaultWriteObject();
        outputStream.writeLong(System.currentTimeMillis());
    }

    private void readObject(ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        System.out.println(new Date(inputStream.readLong()));
    }

    @Override
    public String toString() {
        return "SampleMessage{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
