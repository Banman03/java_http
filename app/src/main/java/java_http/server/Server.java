package java_http.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;
import java.net.Socket;
import java_http.NumericalConstants;
import java_http.utils.*;

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
                socketReadBuffer = clientSocket.getInputStream();
                socketWriteBuffer = clientSocket.getOutputStream();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.out.format("The server has already accepted a connection to the socket at %s.\n", clientSocket.getInetAddress().getHostAddress());
        }
    }

    public void writeData(SocketMessage message) {
        try {
            if (!clientSocket.isOutputShutdown() && !clientSocket.isClosed()) {
                clientSocket.setSendBufferSize(message.getDataLength());
                System.out.format("data size: %d.\n", message.getDataLength());
                socketWriteBuffer.flush();
                socketWriteBuffer.write(message.getData());
                System.out.println("Data sent.\n");
            } else {
                System.out.println("Write stream has already been shut down.\n");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public byte[] readData() {
        int totalBytesRead = 0;
        try {
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

    public void listenCommandLineFromBuffer() {
        while (true) {
            if (!BufferUtils.isBufferEmpty(socketReadBuffer)) {
                byte[] clientData = readData();
                SocketMessage message = new SocketMessage(clientData);
                System.out.format("The server has received: %s.\n", message.dataToString());
                String serverResponse =  "Message received.";
                SocketMessage responseMessage = new SocketMessage(serverResponse.getBytes());
                writeData(responseMessage);
            }
        }
    }

    public ServerSocket getServerSocket() {
        return socket;
    }
}