package java_http.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;

public class Client {
    Socket socket;
    Integer port;
    SocketAddress address;

    public Client() {
        System.out.println("What port should the client be bound to?\n");
        Scanner sc = new Scanner(System.in);
        port = sc.nextInt();

        try {
            socket = new Socket(InetAddress.getLoopbackAddress(), port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        socket.bind(address);
    }
}