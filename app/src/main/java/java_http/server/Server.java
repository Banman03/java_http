package java_http.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java_http.NumericalConstants;
import java_http.http.HttpMethod;
import java_http.utils.*;

public class Server {
    private ServerSocket socket;
    private InetSocketAddress address;
    private Integer port;
    private Socket clientSocket;
    private OutputStream socketWriteBuffer;
    private InputStream socketReadBuffer;
    private byte[] dataToServe;

    
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

    // For now, we will assume that incoming requests follow HTTP/1.0; this will eventually change
    public void httpRequestListener() {
        while (true) {
            if (!BufferUtils.isBufferEmpty(socketReadBuffer)) {
                byte[] clientData = readData();
                
                boolean successfulRequest = processHttpRequest(clientData);
                
                if (successfulRequest) {
                    SocketMessage message = new SocketMessage(dataToServe);
                    writeData(message);
                }
            }
        }
    }

    // ONLY WRITING STRINGS ATM OR ELSE THE HEADER/BODY DELIMITER WONT WORK
    private boolean processHttpRequest(byte[] data) {
        String httpData = printHttpRequest(data);
        String[] requestComponents = httpData.split("'\r''\n''\r''\n'");
        String[] headerComponents = requestComponents[0].split(" "); // if there is no body, requestComponents will have len 1

        HttpMethod method = HttpMethod.parseMethodSafe(headerComponents[0].toUpperCase()).get();
        
        boolean isRequestSuccessful = switch (method) {
            case GET -> {
                yield processGetRequest(headerComponents[1]);
            }
            case POST -> {
                if (requestComponents.length < 2) yield false;
                yield processPostRequest(headerComponents[1], requestComponents[1]);
            }
            case PUT -> {
                if (requestComponents.length < 2) yield false;
                yield processPutRequest(headerComponents[1], requestComponents[1]);
            }
            case DELETE -> {
                yield false;
            }
            case HEAD -> {
                yield false;
            }
        };
        return isRequestSuccessful;
    }

    private boolean processPutRequest(String requestPath, String requestBody) {
        // needs to be idempotent: calling multiple times is the same as calling once
        int lastBackslash = requestPath.lastIndexOf('/');
        String fileName = requestPath.substring(lastBackslash);
        String directoryName = requestPath.replaceAll(fileName, "");
        Path directoryPath = Path.of(directoryName);

        if (canWrite(directoryPath)) {
            Path fullPath = Path.of(requestPath);
            try {
                Files.deleteIfExists(fullPath);
                OutputStream writeStream = Files.newOutputStream(fullPath, StandardOpenOption.WRITE);
                writeStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
                writeStream.close();
                return true;
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return false;
    }

    private boolean processPostRequest(String requestPath, String requestBody) {
        return false;
    }

    private static boolean canWrite(Path directory) {
        if (Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS) && Files.isWritable(directory)) return true;
        return false;
    }
    
    private boolean processGetRequest(String requestPath) {
        Path filePath = Path.of(requestPath);
        if (!isValidFile(filePath)) return false;

        try {
            dataToServe = Files.readAllBytes(filePath);
            return true;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    private static boolean isValidFile(Path filePath) {
        if (Files.exists(filePath, LinkOption.NOFOLLOW_LINKS) && Files.isReadable(filePath) && !Files.isDirectory(filePath, LinkOption.NOFOLLOW_LINKS) && !Files.isSymbolicLink(filePath)) return true;
        return false;
    }

    private static String printHttpRequest(byte[] data) {
        return new String(data, 0, data.length, StandardCharsets.UTF_8);
    }

    public ServerSocket getServerSocket() {
        return socket;
    }
}