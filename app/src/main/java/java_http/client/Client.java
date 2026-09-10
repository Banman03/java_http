package java_http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.net.UnknownHostException;
import java.util.*;
import java_http.NumericalConstants;
import java_http.http.httpUtils.HttpMethod;
import java_http.http.httpUtils.HttpVersion;
import java_http.http.HttpRequest;
import java_http.utils.*;

public class Client implements AutoCloseable {
    private Socket socket;
    private Integer port;
    private InetAddress address;
    private OutputStream socketWriteBuffer;
    private InputStream socketReadBuffer;
    private HttpRequest request;
    private String hostName;
    private final Thread listenerThread;
    private volatile boolean isListenerRunning = true;
    private final String id;

    public Client(String host, int port, String id) {
        this.id = id;
        System.out.format("Binding client to port: %d.\n", port);
        System.out.format("Connecting client to host: %s.\n", host.isBlank() ? "loopback" : host);

        if (!host.isBlank()) {
            try {
                address = InetAddress.getByName(host);
                hostName = host;
            } catch (UnknownHostException e) {
                System.err.println(e.getMessage());
                System.out.println("The host could not be resolved, defaulting to loopback address.\n");
                address = InetAddress.getLoopbackAddress();
                hostName = "loopback";
            }
        } else {
            address = InetAddress.getLoopbackAddress();
            hostName = "loopback";
        }
        
        try {
            socket = new Socket(address, port);
            socketReadBuffer = socket.getInputStream();
            socketWriteBuffer = socket.getOutputStream();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        listenerThread = new Thread(this::listenLoop, "--runnable " + this.id);
        this.listenerThread.setDaemon(true);
        this.listenerThread.start();
    }

    private void listenLoop() {
        while (isListenerRunning) {
            byte[] serverResponse = readData();
            if (serverResponse == null) {
                System.out.format("Server closed connection. Killing client.\n");
                break;
            } else {
                SocketMessage serverResponseSocketMessage = new SocketMessage(serverResponse);
                System.out.format("Client received: %s\n", serverResponseSocketMessage.dataToString());
            }
        }
        close();
    }

    @Override
    public synchronized void close() {
        if (!isListenerRunning) return;
        isListenerRunning = false;
        try {
            this.socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void writeData(SocketMessage message) {
        try {
            if (!socket.isOutputShutdown() && !socket.isClosed()) {
                    socket.setSendBufferSize(message.getDataLength());
                    System.out.format("data size: %d.\n", message.getDataLength());
                    socketWriteBuffer.flush();
                    System.out.println(message.dataToString());
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
                System.out.format("Estimated reading %d bytes.\n", socketReadBuffer.available());
                byte[] input = new byte[NumericalConstants.RECEIVE_BUFFER_SIZE];
                totalBytesRead = socketReadBuffer.read(input);
                System.out.format("Read %d bytes.\n", totalBytesRead);
                return totalBytesRead == -1 ? null : input;
            } else {
                System.out.println("Read stream has already been shut down.\n");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return new byte[0];
    }
    
    public void writeHttpRequest(HttpRequest request) {
        this.request = request;
        writeData(request.getHttpRequestAsSocketMessage());
    }

    private CliUtils isValidRequestLine(String requestLine) {
        String[] headerComponents = requestLine.split(" ");
        System.out.println(Arrays.toString(headerComponents));
        if (requestLine.isBlank() || headerComponents.length != 3)
            return CliUtils.CONTINUE_CLI;

        if (!HttpMethod.parseMethodSafe(headerComponents[0].toUpperCase()).isPresent())
            return CliUtils.CONTINUE_CLI;            
        
        return CliUtils.OTHER;
    }


    private static String[] getRequestHeaders(Scanner sc) {
        System.out.println("Input headers.\n");
        ArrayList<String> product = new ArrayList<>();
        while (true) {
            String header = sc.nextLine();
            if (header.toLowerCase().equals("0x0000"))
                break;
            product.add(header);
        }
        return product.toArray(new String[0]);
    }
    
    private static byte[] getRequestBody(Scanner sc) {
        System.out.println("Input data.\n");
        StringBuilder requestBody = new StringBuilder();

        while (true) {
            String input = sc.nextLine();
            if (input.toLowerCase().equals("0x0000")) 
                break;
            requestBody.append(input);
        }
        
        return requestBody.toString().getBytes(StandardCharsets.US_ASCII);
    }

    private boolean isOpen() {
        return isListenerRunning && this.socket.isConnected() && !this.socket.isClosed();
    }
    
    public void closeSocketLocally() {
        isListenerRunning = false;
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } 
    }

    public Socket getClientSocket() {
        return socket;
    }

    public String getHostName() {
        return hostName;
    }
}