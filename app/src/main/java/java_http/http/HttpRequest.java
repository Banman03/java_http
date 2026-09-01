package java_http.http;

import java.nio.charset.StandardCharsets;

import java_http.utils.SocketMessage;

public class HttpRequest {
    
    private final SocketMessage header;
    private SocketMessage body;

    public HttpRequest(HttpVersion version, HttpMethod method, String URI, byte[] body) throws IllegalArgumentException {
        if (!isValidRequest(method, body)) {
            throw new IllegalArgumentException("Cannot have this combination of method and body");
        }
        
        String headerString = new String(method.getHttpMethod() + " " + URI + " " + version.getHttpVersion());
        this.header = new SocketMessage(headerString.getBytes(StandardCharsets.US_ASCII));
        this.body = new SocketMessage(body);
    }

    private boolean isValidRequest(HttpMethod method, byte[] body) {
        if (body.length > 0 && (method == HttpMethod.GET || method == HttpMethod.HEAD)) return false;
        if (body.length == 0 && (method == HttpMethod.POST || method == HttpMethod.PUT)) return false;
        return true;
    }

    public SocketMessage getHttpRequestAsSocketMessage() {
        byte[] res = new byte[header.getDataLength() + body.getDataLength()];
        System.arraycopy(header.getData(), 0, res, 0, header.getDataLength());
        System.arraycopy(body.getData(), 0, res, header.getDataLength(), body.getDataLength());
        return new SocketMessage(res);
    }
    
}
