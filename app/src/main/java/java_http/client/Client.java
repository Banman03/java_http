package java_http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java_http.NumericalConstants;

public class Client {
    private Socket socket;
    private Integer port;
    private InetAddress address;
    private OutputStream socketWriteBuffer;
    private InputStream socketReadBuffer;

    public Client() {
        System.out.println("What port should the client be bound to?\n");
        Scanner sc = new Scanner(System.in);
        port = sc.nextInt();

        address = InetAddress.getLoopbackAddress();
        try {
            socket = new Socket(address, port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void writeData(String data) {
        try {
            if (socketWriteBuffer == null) {
                socketWriteBuffer = socket.getOutputStream();
            }
            
            if (!socket.isOutputShutdown() && !socket.isClosed()) {
                    socket.setSendBufferSize(data.getBytes().length);
                    socketWriteBuffer.flush();
                    socketWriteBuffer.write(data.getBytes());
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
            if (socketReadBuffer == null) {
                socketReadBuffer = socket.getInputStream();
            }
            
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