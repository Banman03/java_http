package java_http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.net.UnknownHostException;
import java.util.Scanner;
import java_http.NumericalConstants;
import java_http.http.HttpMethod;
import java_http.http.HttpVersion;
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
        port = Integer.parseInt(sc.nextLine());
        System.out.format("Binding client to port: %d.\n", port);

        System.out.println("Input hostname to connect to, or leave empty to connect to localhost.\n");
        String host = sc.nextLine();
        if (!host.equals("\r\n") && !host.equals("\n")) {
            try {
                address = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                System.err.println(e.getMessage());
                System.out.println("The host could not be resolved, defaulting to loopback address.\n");
                address = InetAddress.getLoopbackAddress();
            }
        } else {
            address = InetAddress.getLoopbackAddress();
        }
        
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

    public void writeCommandLine() {
        instantiateListenerThread();
        
        Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine();
            if (input.toLowerCase().equals("exit")) {
                break;
            } else if (input.isBlank()) continue;

            SocketMessage message = new SocketMessage(input.getBytes());
            writeData(message);
        }
    }
    
    public void writeHttpRequest() {
        instantiateListenerThread();

        Scanner sc = new Scanner(System.in);
        while (true) {
            String requestLine = sc.nextLine();
            CliUtils cliState = isValidRequestLine(requestLine);
            if (cliState == CliUtils.CONTINUE_CLI) {
                System.out.println("continuing");
                continue;
            }
            else if (cliState == CliUtils.EXIT_CLI) break;
            writeData(request.getHttpRequestAsSocketMessage());
        }
        System.out.println("Exiting\n");
    }

    private void instantiateListenerThread() {
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
    }

    private CliUtils isValidRequestLine(String requestLine) {
        String[] headerComponents = requestLine.split(" ");
        if (headerComponents[0].toLowerCase().equals("exit"))
            return CliUtils.EXIT_CLI;
        // headerComponents[0] : method; headerComponents[1] : host/path;
        // headerComponents[2] : version
        else if (requestLine.isBlank() || headerComponents.length != 3)
            return CliUtils.CONTINUE_CLI;

        if (!HttpMethod.parseMethodSafe(headerComponents[0].toUpperCase()).isPresent())
            return CliUtils.CONTINUE_CLI;            

        HttpMethod method = HttpMethod.parseMethodSafe(headerComponents[0]).get();
        String URI = new String(headerComponents[1]);
        HttpVersion version = new HttpVersion(headerComponents[2]);

        if (method == HttpMethod.POST || method == HttpMethod.PUT) {
            byte[] data = buildRequestBody();
            request = new HttpRequest(method, URI, version, data);
            return CliUtils.OTHER;
        }
        
        request = new HttpRequest(method, URI, version, null);
        return CliUtils.OTHER;
    }

    private static byte[] buildRequestBody() {
        System.out.println("Input data.\n");
        Scanner sc = new Scanner(System.in);
        StringBuilder requestBody = new StringBuilder();

        while (true) {
            String input = sc.nextLine();
            if (input.toLowerCase().equals("0x0000")) break;
            requestBody.append(input);
        }
        
        return requestBody.toString().getBytes(StandardCharsets.US_ASCII);
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