package java_http.http;

import java.nio.charset.StandardCharsets;

import java_http.http.httpUtils.HttpHeader;
import java_http.http.httpUtils.HttpMethod;
import java_http.http.httpUtils.HttpVersion;
import java_http.utils.SocketMessage;

public class HttpRequest {
    
    private final SocketMessage requestLine;
    private final SocketMessage header;
    private SocketMessage body;

    public HttpRequest(HttpMethod method, String URI, HttpVersion version, String[] headerLine, byte[] body) throws IllegalArgumentException {
        if (!isValidRequest(method, body)) {
            throw new IllegalArgumentException("Cannot have this combination of method and body");
        }
        
        String requestString = new String(method.getHttpMethod() + " " + URI + " " + version.getHttpVersion() + "\r\n");
        this.requestLine = new SocketMessage(requestString.getBytes(StandardCharsets.US_ASCII));
        HttpHeader headerObject = new HttpHeader(headerLine);
        this.header = new SocketMessage(headerObject.getHttpKVPAsByte());
        
        if (body == null) this.body = new SocketMessage(new byte[0]);
        else this.body = new SocketMessage(body);
    }

    private boolean isValidRequest(HttpMethod method, byte[] body) {
        if (body != null && body.length > 0 && (method == HttpMethod.GET || method == HttpMethod.HEAD)) return false;
        if (body == null && (method == HttpMethod.POST || method == HttpMethod.PUT)) return false;
        return true;
    }

    public static byte[] combineByteArrays(byte[]... byteArrays) {
        int totalLength = 0;
        for (byte[] b : byteArrays) {
            totalLength += b.length;
        }
        byte[] product = new byte[totalLength];
        int bytesCopied = 0;
        for (byte[] b : byteArrays) {
            System.arraycopy(b, 0, product, bytesCopied, b.length);
            bytesCopied += b.length;
        }
        return product;
    }

    public SocketMessage getHttpRequestAsSocketMessage() {
        byte[] res = combineByteArrays(requestLine.getData(), header.getData(), body.getData());
        return new SocketMessage(res);
    }
}
