package java_http.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;
import java.net.Socket;
import java_http.NumericalConstants;

public class Server {
    private ServerSocket socket;
    private InetSocketAddress address;
    private Integer port;
    private Socket clientSocket;
    private OutputStream socketWriteBuffer;
    private InputStream socketReadBuffer;

    
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

    public void writeData(String data) {
        try {
            if (socketWriteBuffer == null) {
                socketWriteBuffer = clientSocket.getOutputStream();
            }

            if (!clientSocket.isOutputShutdown() && !socket.isClosed()) {
                clientSocket.setSendBufferSize(data.getBytes().length);
                socketWriteBuffer.flush();
                socketWriteBuffer.write(data.getBytes());
                System.out.println("(Server) Data sent.\n");
            } else {
                System.out.println("(Server) Write stream has already been shut down.\n");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public byte[] readData() {
        int totalBytesRead = 0;
        try {
            if (socketReadBuffer == null) {
                socketReadBuffer = clientSocket.getInputStream();
            }

            if (!clientSocket.isInputShutdown() && !clientSocket.isClosed()) {
                clientSocket.setReceiveBufferSize(NumericalConstants.RECEIVE_BUFFER_SIZE);
                System.out.format("(Server) Estimated reading %d bytes.\n", socketReadBuffer.available());
                byte[] input = new byte[NumericalConstants.RECEIVE_BUFFER_SIZE];
                totalBytesRead = socketReadBuffer.read(input);
                System.out.format("(Server) Read %d bytes.\n", totalBytesRead);
                return input;
            } else {
                System.out.println("(Server) Read stream has already been shut down.\n");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        if (totalBytesRead == 0) {
            System.out.println("0 bytes were read. Error somewhere?\n");
        }
        return new byte[0];
    }

    public ServerSocket getServerSocket() {
        return socket;
    }
}