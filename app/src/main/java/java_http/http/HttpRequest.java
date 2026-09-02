package java_http.http;

import java.nio.charset.StandardCharsets;

import java_http.utils.SocketMessage;

public class HttpRequest {
    
    private final SocketMessage requestLine;
    private final SocketMessage header;
    private SocketMessage body;

    public HttpRequest(HttpMethod method, String URI, HttpVersion version, byte[] body) throws IllegalArgumentException {
        if (!isValidRequest(method, body)) {
            throw new IllegalArgumentException("Cannot have this combination of method and body");
        }
        
        String headerString = new String(method.getHttpMethod() + " " + URI + " " + version.getHttpVersion());
        this.header = new SocketMessage(headerString.getBytes(StandardCharsets.US_ASCII));
        if (body == null) this.body = new SocketMessage(new byte[0]);
        else {
            byte[] delimiter = {'\r', '\n', '\r', '\n'};
            byte[] compliantBody = combineByteArrays(delimiter, body);
            this.body = new SocketMessage(compliantBody);
        }

    }

    private boolean isValidRequest(HttpMethod method, byte[] body) {
        if (body != null && body.length > 0 && (method == HttpMethod.GET || method == HttpMethod.HEAD)) return false;
        if (body == null && (method == HttpMethod.POST || method == HttpMethod.PUT)) return false;
        return true;
    }

    public static byte[] combineByteArrays(byte[] a, byte[] b) {
        byte[] finalArray = new byte[a.length + b.length];
        System.arraycopy(a, 0, finalArray, 0, a.length);
        System.arraycopy(b, 0, finalArray, a.length, b.length);
        return finalArray;
    }

    public SocketMessage getHttpRequestAsSocketMessage() {
        byte[] res = combineByteArrays(header.getData(), body.getData());
        return new SocketMessage(res);
    }
}
