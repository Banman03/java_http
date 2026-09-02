package java_http.http;

import java.nio.charset.StandardCharsets;

import java_http.utils.SocketMessage;

public class HttpResponse {
    private final SocketMessage header;
    private SocketMessage body;

    public HttpResponse(HttpVersion version, Integer responseCode, String details, byte[] body) {
        String headerString = new String(version.getHttpVersion() + " " + responseCode.toString() + " " + details);
        this.header = new SocketMessage(headerString.getBytes(StandardCharsets.US_ASCII));

        if (body == null)
            this.body = new SocketMessage(new byte[0]);
        else {
            byte[] delimiter = { '\r', '\n', '\r', '\n' };
            byte[] compliantBody = combineByteArrays(delimiter, body);
            this.body = new SocketMessage(compliantBody);
        }
    }

    public static byte[] combineByteArrays(byte[] a, byte[] b) {
        byte[] finalArray = new byte[a.length + b.length];
        System.arraycopy(a, 0, finalArray, 0, a.length);
        System.arraycopy(b, 0, finalArray, a.length, b.length);
        return finalArray;
    }

    public SocketMessage getHttpResponseAsSocketMessage() {
        byte[] res = combineByteArrays(header.getData(), body.getData());
        return new SocketMessage(res);
    }
}