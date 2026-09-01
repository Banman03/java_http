package java_http.http;

import java.nio.charset.StandardCharsets;

import java_http.utils.SocketMessage;

public class HttpRequest {
    
    private final SocketMessage header;
    private SocketMessage body;

    public HttpRequest(HttpVersion version, HttpMethod method, String URI, byte[] body) {
        String headerString = new String(method.getHttpMethod() + " " + URI + " " + version.getHttpVersion());
        this.header = new SocketMessage(headerString.getBytes(StandardCharsets.US_ASCII));
        this.body = new SocketMessage(body);
    }

    public HttpRequest(HttpVersion version, HttpMethod method, String URI) {
        String headerString = new String(method.getHttpMethod() + " " + URI + " " + version.getHttpVersion());
        this.header = new SocketMessage(headerString.getBytes(StandardCharsets.US_ASCII));
    }

    
}
