package java_http.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;
import java.net.Socket;

public class Server {
    private ServerSocket socket;
    private InetSocketAddress address;
    private Integer port;
    private Socket clientSocket;

    
    public Server() {
        System.out.println("What port should the server be bound to?\n");
        Scanner sc = new Scanner(System.in);
        port = sc.nextInt();

        try {
            socket = new ServerSocket();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        address = new InetSocketAddress(port);
    }

    public void bindLocally() {
        if (socket != null && !socket.isBound()) {
            try {
                socket.bind(address);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            System.out.format("Socket successfully bound to port %d and local address %s.\n", port, address.getAddress().getHostAddress());
        } else {
            System.out.format("Socket at port %d is unbound.\n", port);
        }
    }

    public void closeLocally() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                System.out.println("Socket successfully closed.\n");
            } else {
                System.out.format("Socket at port %d is already closed.\n", port);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    } 

    public void acceptLocally() {
        if (clientSocket == null) {
            try {
                clientSocket = socket.accept();
                System.out.format("Conencted to the client socket at %s.\n", clientSocket.getInetAddress().getHostAddress());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.out.format("The server has already accepted a connection to the socket at %s.\n", clientSocket.getInetAddress().getHostAddress());
        }
    }

    public ServerSocket getServerSocket() {
        return socket;
    }
}