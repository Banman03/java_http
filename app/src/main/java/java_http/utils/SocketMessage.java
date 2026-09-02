package java_http.utils;

import java.nio.charset.StandardCharsets;

public class SocketMessage {
    private int dataLength;
    private byte[] data;

    public SocketMessage() {
        dataLength = 0;
        data = new byte[0];
    }

    public SocketMessage(byte[] data) {
        dataLength = data.length;
        this.data = data;
    }

    public String dataToString() {
        return new String(data, 0, dataLength, StandardCharsets.UTF_8);
    }

    public void addData(byte[] data) {
        dataLength = data.length;
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public int getDataLength() {
        return dataLength;
    }
}