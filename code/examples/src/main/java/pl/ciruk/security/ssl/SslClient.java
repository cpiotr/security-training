package pl.ciruk.security.ssl;

import com.google.common.base.Preconditions;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class SslClient {
    private final String host;
    private final int port;

    public SslClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        String message = Arrays.stream(args)
                .collect(joining(" "));
        new SslClient("localhost", 1234).send(message);
    }

    public void send(String text) throws Exception {
        try (SSLSocket socket = createSSLSocket();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            System.out.println("Connected to: " + new SocketIdentity(socket).getCommonNameOrDefault("Anonymous"));

            writer.write(formatAsLine(text));
            writer.flush();

            String response = reader.readLine();
            String expectedResponse = text+"ACK";
            Preconditions.checkState(expectedResponse.equals(response));

            System.out.println("Received ACK");
        }
    }

    private String formatAsLine(String text) {
        return text + "\n";
    }

    private SSLSocket createSSLSocket() throws Exception {
        SSLContext context = SSLContext.getDefault();
        SSLSocketFactory factory = context.getSocketFactory();
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
        return socket;
    }
}
