package pl.ciruk.security.serial;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class SerializationMechanism {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        SampleMessage message = new SampleMessage("MyTitle", "MyBody");
        System.out.println("message = " + message);

        Path file = Files.createTempFile("serial", "temp");
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file.toFile()));
             ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file.toFile()))
        ) {
            outputStream.writeObject(message);

            SampleMessage deserialized = (SampleMessage) inputStream.readObject();
            System.out.println("deserialized = " + deserialized);
        }

    }
}
