package pl.ciruk.security.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;

public class SslServer {

    private final int port;

    public SslServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        new SslServer(1234).run();
    }

    public void run() throws Exception {
        ServerSocket socket = createSSLSocket(port);

        System.out.println("Server started. Awaiting client...");

        try (SSLSocket client = (SSLSocket) socket.accept();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()))
        ) {
            System.out.println("Client connected: " + new SocketIdentity(client).getCommonNameOrDefault("ANONYMOUS"));

            String message = reader.readLine();
            System.out.println("Received: " + message);

            writer.write(acknowledge(message));
            writer.flush();

            System.out.println("ACK sent");
        }
    }

    private String acknowledge(String message) {
        return message + "ACK";
    }

    private ServerSocket createSSLSocket(int port) throws Exception {
        SSLContext context = SSLContext.getDefault();
        SSLServerSocketFactory factory = context.getServerSocketFactory();
        SSLServerSocket socket = (SSLServerSocket) factory.createServerSocket(port);
        socket.setNeedClientAuth(true);
        return socket;
    }

}