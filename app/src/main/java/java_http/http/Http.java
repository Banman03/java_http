package java_http.http;

import java_http.utils.SocketMessage;

public abstract class Http {
    private final HttpType type;
    private byte[] header;
    private byte[] body;
    private byte[] message;

    public Http(HttpType type) {
        this.type = type;
    }

    public HttpType getType() {
        return type;
    }

    protected abstract void buildHeader();

    public abstract void buildMessage(SocketMessage message);
}