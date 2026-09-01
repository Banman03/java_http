package java_http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java_http.NumericalConstants;
import java_http.http.HttpMethod;
import java_http.http.HttpVersion;
import java_http.http.HttpMethod.*;
import java_http.http.HttpRequest;
import java_http.utils.*;

public class Client {
    private Socket socket;
    private Integer port;
    private InetAddress address;
    private OutputStream socketWriteBuffer;
    private InputStream socketReadBuffer;
    private HttpRequest request;

    public Client() {
        System.out.println("What port should the client be bound to?\n");
        Scanner sc = new Scanner(System.in);
        port = sc.nextInt();
        System.out.format("Binding client to port: %d.\n", port);

        address = InetAddress.getLoopbackAddress();
        try {
            socket = new Socket(address, port);
            socketReadBuffer = socket.getInputStream();
            socketWriteBuffer = socket.getOutputStream();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void writeData(SocketMessage message) {
        try {
            if (!socket.isOutputShutdown() && !socket.isClosed()) {
                    socket.setSendBufferSize(message.getDataLength());
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
            if (!socket.isInputShutdown() && !socket.isClosed()) {
                socket.setReceiveBufferSize(NumericalConstants.RECEIVE_BUFFER_SIZE);
                System.out.format("Estimated reading %d bytes.\n", socketReadBuffer.available());
                byte[] input = new byte[NumericalConstants.RECEIVE_BUFFER_SIZE];
                totalBytesRead = socketReadBuffer.read(input);
                System.out.format("Read %d bytes.\n", totalBytesRead);
                return input;
            } else {
                System.out.println("Read stream has already been shut down.\n");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        if (totalBytesRead == 0) {
            System.out.println("0 bytes were read. Error somewhere?\n");
        }
        return new byte[0];
    }

    public void writeCommandLineToBuffer() {
        Thread serverResponseThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] serverResponse = readData();
                if (serverResponse == null) {
                    break;
                }
                SocketMessage message = new SocketMessage(serverResponse);
                System.out.format("Client received: %s\n", message.dataToString());
            }
        });

        serverResponseThread.setDaemon(true);
        serverResponseThread.start();
        
        Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine();
            if (input.toLowerCase().equals("exit")) {
                break;
            } else if (input.isBlank()) continue;

            SocketMessage message = new SocketMessage(input.getBytes());
            writeData(message);
        }
        System.out.println("Exiting\n");
    }

    public void writeHttpRequest() {
        Thread serverResponseThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] serverResponse = readData();
                if (serverResponse == null) {
                    break;
                }
                SocketMessage message = new SocketMessage(serverResponse);
                System.out.format("HTTP Response: %s\n", message.dataToString());
            }
        });

        serverResponseThread.setDaemon(true);
        serverResponseThread.start();

        Scanner sc = new Scanner(System.in);
        while (true) {
            String requestLine = sc.nextLine();
            if (!isValidRequestLine(requestLine)) continue;
            
            writeData(request.getHttpRequestAsSocketMessage());
        }
    }

    private boolean isValidRequestLine(String requestLine) {
        String[] headerComponents = requestLine.split(" ");
        if (requestLine.toLowerCase().equals("exit"))
            return false;
        // headerComponents[0] : method; headerComponents[1] : host/path;
        // headerComponents[2] : version
        else if (requestLine.isBlank() || headerComponents.length != 3)
            return false;

        if (!HttpMethod.parseMethodSafe(headerComponents[0]).isPresent())
            return false;

        HttpMethod method = HttpMethod.parseMethodSafe(headerComponents[0]).get();
        String URI = new String(headerComponents[1]);
        HttpVersion version = new HttpVersion(headerComponents[2]);
        
        request = new HttpRequest(method, URI, version, null);
        return true;
    }
    
    public void closeSocketLocally() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } 
    }

    public Socket getClientSocket() {
        return socket;
    }
}