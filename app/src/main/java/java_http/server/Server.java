package java_http.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.Scanner;

public class Server {
    private ServerSocket socket;
    private InetSocketAddress address;
    private Integer port;
    
    public Server() {
        System.out.println("What port should the server be bound to?\n");
        Scanner sc = new Scanner(System.in);
        port = sc.nextInt();
        sc.close();

        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        try {
            address = new InetSocketAddress(InetAddress.getByName(""), port);
        } catch (UnknownHostException e) {
            System.err.println(e.getMessage());
        }
    }

    public void bindLocally() {
        try {
            socket.bind(address);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void closeLocally() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    ServerSocket getServerSocket() {
        return socket;
    }
}